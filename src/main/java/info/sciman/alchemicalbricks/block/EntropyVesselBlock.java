package info.sciman.alchemicalbricks.block;

import info.sciman.alchemicalbricks.block.entity.AbstractEntropyContainerBlockEntity;
import info.sciman.alchemicalbricks.block.entity.AlchemicAltarBlockEntity;
import info.sciman.alchemicalbricks.block.entity.EntropyVesselBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EntropyVesselBlock extends BlockWithEntity {

    public static final IntProperty ENTROPY = IntProperty.of("entropy",0,4);

    public EntropyVesselBlock(Settings settings) {
        super(settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)this.getStateManager().getDefaultState()).with(ENTROPY,0)));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        //With inheriting from BlockWithEntity this defaults to INVISIBLE, so we need to change that!
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new EntropyVesselBlockEntity();
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ENTROPY);
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity be = world.getBlockEntity(pos);
        if (be != null && be instanceof EntropyVesselBlockEntity) {
            return (int) ((((float)((EntropyVesselBlockEntity)be).getEntropy()) / ((EntropyVesselBlockEntity) be).getEntropyCapacity()) * 15);
        }else {
            return 0;
        }
    }
}
