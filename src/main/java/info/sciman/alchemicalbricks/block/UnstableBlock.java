package info.sciman.alchemicalbricks.block;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import info.sciman.alchemicalbricks.block.entity.UnstableBlockEntity;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Random;

// TODO: make these drop something other than bricks
// Having them replicate so easily is mega busted
// also: change renderer. it's crazy laggy rn
public class UnstableBlock extends Block implements BlockEntityProvider {

    private static final int MAX_AGE = 5;
    private static final Identifier TAG_PROTECTED = AlchemicalBricksMod.id("unstable_protected");
    private static final Tag<Block> PROTECTED_BLOCKS;
    private static final int SPREAD_DELAY_MIN = 60;
    private static final int SPREAD_DELAY_MAX = 120;

    public static final IntProperty AGE = IntProperty.of("age",0,MAX_AGE);

    static {
        // Get protected blocks
        PROTECTED_BLOCKS = TagRegistry.block(TAG_PROTECTED);
    }

    public UnstableBlock(Settings settings) {
        super(settings);
        // Setup property
        this.setDefaultState(this.getStateManager().getDefaultState().with(AGE,0));
    }

    public static boolean canSpreadToBlock(BlockState block) {
        return (!block.isAir() &&
                block.getFluidState().getFluid() == Fluids.EMPTY &&
                !PROTECTED_BLOCKS.contains(block.getBlock()) &&
                block.getBlock() != AlchemicalBricksMod.UNSTABLE_BLOCK);
    }

    // Give effect to those that step on us
    public void onSteppedOn(World world, BlockPos pos, Entity entity) {
        if (!entity.isFireImmune() && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            ((LivingEntity) entity).addStatusEffect(new StatusEffectInstance(AlchemicalBricksMod.INSTABILITY,200));
        }

        super.onSteppedOn(world, pos, entity);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    private int getSpreadDelay(World world) {
        return world.random.nextInt(SPREAD_DELAY_MAX-SPREAD_DELAY_MIN)+SPREAD_DELAY_MIN+1;
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);

        BlockPos newPos = findValidAdjacentBlock((World) world,pos);
        // Move
        if (!newPos.equals(pos)) {
            world.setBlockState(newPos,this.getDefaultState());
            world.getBlockTickScheduler().schedule(newPos,this,getSpreadDelay(world));
        }
    }

    // Schedule initial tick event
    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        world.getBlockTickScheduler().schedule(pos, this, getSpreadDelay(world));
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        super.scheduledTick(state, world, pos, random);

        BlockPos newPos = findValidAdjacentBlock((World) world,pos);
        // Clone and age
        int newAge = state.get(AGE)+1;
        if (newPos != pos && newAge <= MAX_AGE) {
            // Spread
            world.setBlockState(newPos,state.with(AGE, newAge));
            // Schedule another delay
            world.getBlockTickScheduler().schedule(newPos, this, getSpreadDelay(world));
            world.getBlockTickScheduler().schedule(pos, this, getSpreadDelay(world));
        }else{
            // Just get rid of it
            world.setBlockState(pos,Blocks.AIR.getDefaultState());
        }
    }

    private BlockPos findValidAdjacentBlock(World world, BlockPos original) {

        ArrayList<BlockPos> validPos = new ArrayList<>();

        // Loop over adjacent blocks
        for (int x=-1;x<2;x++) {
            for (int y=-1;y<2;y++) {
                for (int z=-1;z<2;z++) {
                    // Don't check the center
                    if (!(x == 0 && z == 0 && y == 0)) {
                        // Check for valid block
                        BlockPos p = original.add(x,y,z);
                        BlockState s = world.getBlockState(p);
                        if (canSpreadToBlock(s)) {
                            validPos.add(p);
                        }
                    }
                }
            }
        }
        if (validPos.isEmpty()) {
            return original;
        }else{
            // Return random position
            return validPos.get(world.random.nextInt(validPos.size()));
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new UnstableBlockEntity();
    }
}
