package info.sciman.alchemicalbricks.block;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import info.sciman.alchemicalbricks.block.entity.EntropySpongeBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntropySpongeBlock extends BlockWithEntity {

    public static final BooleanProperty FULL = BooleanProperty.of("full");

    public EntropySpongeBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FULL,false));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        //With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    // Release entropy on breaking
    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof EntropySpongeBlockEntity) {

                int entropy = ((EntropySpongeBlockEntity) blockEntity).getEntropy();
                System.out.println(entropy);
                if (entropy > 10) {
                    // Calculate percentage
                    int maxEntropy = ((EntropySpongeBlockEntity) blockEntity).getEntropyCapacity();
                    float percent = ((float) entropy) / maxEntropy;

                    // Get overlap box
                    Box overlapBox = new Box(pos).expand(percent * 32);
                    System.out.println(overlapBox.getAverageSideLength());
                    List<LivingEntity> list = world.getNonSpectatingEntities(LivingEntity.class, overlapBox);

                    int effectLevel = (int) (percent * 2);

                    // Apply effect to all overlapping entities
                    for (LivingEntity e : list) {
                        e.addStatusEffect(new StatusEffectInstance(AlchemicalBricksMod.INSTABILITY,600,effectLevel));
                        System.out.println(e.getDisplayName().toString());
                    }

                }

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
        builder.add(FULL);
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
