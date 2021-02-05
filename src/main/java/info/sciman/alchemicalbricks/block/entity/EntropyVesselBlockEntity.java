package info.sciman.alchemicalbricks.block.entity;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;

public class EntropyVesselBlockEntity extends AbstractEntropyContainerBlockEntity {

    public EntropyVesselBlockEntity() {
        super(AlchemicalBricksMod.ENTROPY_VESSEL_ENTITY);
    }

    @Override
    public int getEntropyCapacity() {
        return 250;
    }

    @Override
    public boolean addEntropy(int amt) {
        return super.addEntropy(amt);
    }

    @Override
    public boolean canAcceptFromAltar() {
        return true;
    }

    @Override
    void onEntropyOverflow() {
        if (!world.isClient()) {
            // Break this block
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
