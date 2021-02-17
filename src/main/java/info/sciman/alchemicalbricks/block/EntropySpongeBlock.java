package info.sciman.alchemicalbricks.block;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import info.sciman.alchemicalbricks.block.entity.EntropySpongeBlockEntity;
import info.sciman.alchemicalbricks.util.AlchemyHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntropySpongeBlock extends BlockWithEntity {

    public static final IntProperty FULLNNESS = IntProperty.of("fullness",0,3);

    public EntropySpongeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FULLNNESS,0));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        //With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    // Get blockentity data
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);

        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof EntropySpongeBlockEntity) {
            ((EntropySpongeBlockEntity) blockEntity).addEntropy(0);
        }

    }

    // Release entropy on breaking
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof EntropySpongeBlockEntity) {

                int entropy = ((EntropySpongeBlockEntity) blockEntity).getEntropy();
                /*if (entropy > 10) {
                    // Calculate percentage
                    int maxEntropy = ((EntropySpongeBlockEntity) blockEntity).getEntropyCapacity();
                    float percent = ((float) entropy) / maxEntropy;

                    // Create effect
                    if (world.isClient()) {
                        for (int i=0;i<entropy;i++) {
                            world.addParticle(ParticleTypes.SMOKE,pos.getX()+0.5,pos.getY()+0.5,pos.getZ()+0.5,
                                    world.random.nextDouble()*4-2,
                                    world.random.nextDouble()*4-2,
                                    world.random.nextDouble()*4-2
                            );
                        }
                    }

                    // Get overlap box
                    Box overlapBox = new Box(pos).expand(percent * 8);
                    List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, overlapBox);

                    int effectLevel = (int) (percent);

                    // Apply effect to all overlapping entities
                    for (LivingEntity e : list) {
                        e.addStatusEffect(new StatusEffectInstance(AlchemicalBricksMod.INSTABILITY,600,effectLevel));
                        System.out.println(e.getDisplayName().toString());
                    }

                }*/

                AlchemyHelper.onEntropyReleased(world,pos,entropy);

                // update comparators
                world.updateComparators(pos,this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new EntropySpongeBlockEntity();
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FULLNNESS);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be != null && be instanceof EntropySpongeBlockEntity) {
            return (int) ((((float)((EntropySpongeBlockEntity)be).getEntropy()) / ((EntropySpongeBlockEntity) be).getEntropyCapacity()) * 15);
        }else {
            return 0;
        }
    }
}
