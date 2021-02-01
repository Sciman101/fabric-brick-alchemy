package info.sciman.alchemicalbricks.block.entity;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.explosion.Explosion;

public class EntropyVesselBlockEntity extends AbstractEntropyContainerBlockEntity {

    private int entropy = 0;

    public EntropyVesselBlockEntity() {
        super(AlchemicalBricksMod.ENTROPY_VESSEL_ENTITY);
    }

    public int getEntropy() {
        return entropy;
    }

    @Override
    public int getEntropyCapacity() {
        return 250;
    }

    @Override
    public boolean canAcceptFromAltar() {
        return true;
    }

    @Override
    void onEntropyOverflow() {
        if (!world.isClient()) {
            world.breakBlock(pos, false);
        }
    }

    // Retrieve values from tag
    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
        entropy = tag.getInt("entropy");
    }

    // Save values in compound tag
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putInt("entropy",entropy);

        return tag;
    }

}
