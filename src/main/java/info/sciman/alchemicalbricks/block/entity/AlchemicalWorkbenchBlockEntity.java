package info.sciman.alchemicalbricks.block.entity;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import info.sciman.alchemicalbricks.recipe.TransmutationRecipe;
import info.sciman.alchemicalbricks.screen.AlchemicalWorkbenchScreenHandler;
import info.sciman.alchemicalbricks.util.ImplementedInventory;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.tag.Tag;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Random;

public class AlchemicalWorkbenchBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory, SidedInventory, Tickable {

    public static final int MAX_ENTROPY = 100;
    // Everything the altar pillars can be made of
    private static final Tag<Block> PILLAR_BLOCKS;
    static {
        PILLAR_BLOCKS = TagRegistry.block(AlchemicalBricksMod.id("alchemical_bricks"));
    }

    // Multiblock helpers
    private static final BlockPos[] PILLAR_OFFSETS = new BlockPos[]{
            new BlockPos(3,2,0),
            new BlockPos(-3,2,0),
            new BlockPos(0,2,-3),
            new BlockPos(0,2,3),
            new BlockPos(2,1,2),
            new BlockPos(2,1,-2),
            new BlockPos(-2,1,-2),
            new BlockPos(-2,1,2)
    };

    private Text customName;

    // How much entropy does the workbench contain? from 0-100
    private int entropy = 0;
    private int conversionProgress = 0;
    // How many pillars are there?
    private int numPillars = 0;
    private byte pillarArrangement;
    // Used to store input/output
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(2, ItemStack.EMPTY);

    // Used for transmutation
    private Item prevInputItem = null; // What was the last thing we had input?
    private TransmutationRecipe cachedRecipe; // This is what we will produce
    private boolean transmuting = false;

    // PropertyDelegate to sync values
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            if (index == 0) {
                return entropy;
            }else if (index == 1) {
                return conversionProgress;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                entropy = value;
            }else if (index == 1) {
                conversionProgress = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    // Default constructor
    public AlchemicalWorkbenchBlockEntity() {
        super(AlchemicalBricksMod.ALCHEMICAL_WORKBENCH_ENTITY);
    }

    public void setCustomName(Text customName) {
        this.customName = customName;
    }
    public Text getName() {
        return this.customName != null ? this.customName : this.getContainerName();
    }
    public Text getDisplayName() {
        return this.getName();
    }
    @Nullable
    public Text getCustomName() {
        return this.customName;
    }
    // Default name
    protected Text getContainerName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AlchemicalWorkbenchScreenHandler(syncId,inv,this,propertyDelegate);
    }

    @Override
    public void tick() {

        boolean doParticles = false;
        if (!world.isClient()) {

            ItemStack inputStack = getItems().get(0);
            // If we aren't already transmuting, try and find a valid transmutation
            if (!transmuting) {
                if (!inputStack.isEmpty()) {
                    // Check for a change in item
                    if (inputStack.getItem() != prevInputItem) {

                        // Try and perform transmutation
                        SimpleInventory inventory = new SimpleInventory(inputStack);
                        Optional<TransmutationRecipe> match = world.getRecipeManager().getFirstMatch(TransmutationRecipe.Type.INSTANCE,inventory,world);

                        // this should only get called when an item is swapped in the slot
                        if (match.isPresent()) {

                            TransmutationRecipe.AlchemyContext ctx = match.get().getContext();
                            if (ctx == TransmutationRecipe.AlchemyContext.ALTAR ||
                                ctx == TransmutationRecipe.AlchemyContext.ANY) {
                                // Cache recipe
                                cachedRecipe = match.get();
                                transmuting = true;
                                conversionProgress = 0;
                            }
                        }
                    }
                    prevInputItem = inputStack.getItem();
                }
            }else{

                doParticles = true;

                // First, check for item removal
                if (inputStack.isEmpty() || inputStack.getItem() != prevInputItem) {
                    // Reset conversion
                    transmuting = false;
                    conversionProgress = 0;
                    prevInputItem = null;
                    cachedRecipe = null;
                }else{

                    ItemStack outputStack = getItems().get(1);

                    if (numPillars > 0) {

                        // Make sure the output matches the cached output
                        if ((outputStack.getItem() == cachedRecipe.getOutput().getItem() || outputStack.isEmpty()) && outputStack.getCount() + 1 <= outputStack.getMaxCount()) {
                            // Increment progress based on the number of pillars surrounding the altar
                            if (world.getTime() % ((9 - numPillars) * 2L) == 0L) {
                                conversionProgress++;
                                if (conversionProgress > 24) {
                                    // Transmutation successful!

                                    // Decrement input stack
                                    inputStack.decrement(1);
                                    // Set output
                                    if (outputStack.isEmpty()) {
                                        getItems().set(1,cachedRecipe.getOutput().copy());
                                    }else {
                                        outputStack.increment(1);
                                    }

                                    // Reset progress
                                    conversionProgress = 0;

                                    // Add entropy
                                    this.entropy += cachedRecipe.getEntropy();

                                    // Uh oh
                                    if (entropy > MAX_ENTROPY) {
                                        BlockPos pos = getPos();
                                        this.world.createExplosion(null,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,8f, Explosion.DestructionType.DESTROY);
                                    }
                                }
                            }
                        }
                    }

                    prevInputItem = inputStack.getItem();
                }
            }
        }else{
            // Client effects
            if (numPillars > 0) {
                BlockPos ppos, pos;
                pos = getPos();
                boolean pillarExists;
                Random random = world.random;
                // Create particle streams
                for (int i=0;i<8;i++) {
                    pillarExists = ((pillarArrangement >> i) & 1) != 0;
                    if (pillarExists) {
                        ppos = pos.add(PILLAR_OFFSETS[i]);
                        world.addParticle(ParticleTypes.ENCHANT,
                                pos.getX()+.5,
                                pos.getY()+.75,
                                pos.getZ()+.5,
                                -pos.getX()+ppos.getX(),
                                -pos.getY()+ppos.getY(),
                                -pos.getZ()+ppos.getZ());
                    }
                }
            }
        }


        // Poll for multiblock structure
        if (this.world.getTime() % 40L == 0L) {
            updatePillarCount();
        }
    }


    // Get the number of columns surrounding the workbench
    private void updatePillarCount() {
        numPillars = 0;
        pillarArrangement = 0;
        int i,j;
        for (i=0;i<8;i++) {
            BlockPos offset = PILLAR_OFFSETS[i];
            BlockPos pillarPos = getPos().add(offset);

            int y=0;
            // Figure out if the pillar shape is complete
            for (j=0;j<=offset.getY();j++) {
                y = offset.getY()-j;
                if (y != 0) {
                    // Check for the main body of the pillar
                    if (!PILLAR_BLOCKS.contains(world.getBlockState(pillarPos.down(y)).getBlock())) {
                        break;
                    }
                }else{
                    // Check for the tip of the pillar
                    boolean pillarExists = world.getBlockState(pillarPos).getBlock() == Blocks.SOUL_LANTERN;
                    if (pillarExists) {
                        pillarArrangement |= (1 << i);
                        numPillars++;
                    }
                }
            }
        }
    }


    // Retrieve values from tag
    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        entropy = tag.getInt("entropy");
        Inventories.fromTag(tag,items);
    }

    // Save values in compound tag
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putInt("entropy",entropy);
        Inventories.toTag(tag,items);

        return tag;
    }

    // Get all stored items
    @Override
    public DefaultedList<ItemStack> getItems() {
        return items;
    }

    // Used for slot sides
    @Override
    public int[] getAvailableSlots(Direction side) {
        // Just return an array of all slots
        int[] result = new int[getItems().size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }
        return result;
    }
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot == 0;
    }
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return dir == Direction.DOWN && slot == 1;
    }
}
