package info.sciman.alchemicalbricks.block.entity;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import info.sciman.alchemicalbricks.block.AlchemicAltarBlock;
import info.sciman.alchemicalbricks.block.EntropySpongeBlock;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundTag;

public class EntropySpongeBlockEntity extends AbstractEntropyContainerBlockEntity {

    public EntropySpongeBlockEntity() {
        super(AlchemicalBricksMod.ENTROPY_VESSEL_ENTITY);
    }

    @Override
    public int getEntropyCapacity() {
        return 250;
    }

    @Override
    public boolean addEntropy(int amt) {
        boolean result = super.addEntropy(amt);

        // Update block state
        BlockState bs = this.world.getBlockState(pos);
        if (getEntropy() > 0 && !bs.get(EntropySpongeBlock.FULL)) {
            world.setBlockState(pos,bs.with(EntropySpongeBlock.FULL,true));
            markDirty();
        }else if (getEntropy() == 0 && bs.get(EntropySpongeBlock.FULL)) {
            world.setBlockState(pos,bs.with(EntropySpongeBlock.FULL,false));
            markDirty();
        }

        return result;
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
