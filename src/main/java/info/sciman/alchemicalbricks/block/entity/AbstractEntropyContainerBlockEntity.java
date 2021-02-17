package info.sciman.alchemicalbricks.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

public abstract class AbstractEntropyContainerBlockEntity extends BlockEntity {

    protected int entropy = 0;

    public AbstractEntropyContainerBlockEntity(BlockEntityType<?> type) {
        super(type);
    }

    public int getEntropy() {
        return entropy;
    }

    /**
     * How much entropy can this container hold
     * @return
     */
    abstract public int getEntropyCapacity();

    /**
     * Add entropy to the container
     * @param amt
     * @return whether or not we exceeded capacity
     */
    public boolean addEntropy(int amt) {
        entropy += amt;

        if (entropy > getEntropyCapacity()) {
            onEntropyOverflow();
            return true;
        }else if (entropy < 0) {
            entropy = 0;
        }
        return false;
    }

    /**
     * Called when the container overflows with entropy
     */
    abstract void onEntropyOverflow();

    /**
     * Can this container accept entropy from an adjacent altar?
     * @return
     */
    public boolean canAcceptFromAltar() {
        return false;
    }

    /**
     * Transfer entropy to an adjacent container
     */
    public void tryTransferEntropy() {
        if (getEntropy() > 0) {
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        if (!(x == 0 && y == 0 && z == 0)) {
                            // Look for block entity
                            BlockEntity be = world.getBlockEntity(pos.add(x, y, z));
                            if (be != null && be instanceof AbstractEntropyContainerBlockEntity) {
                                AbstractEntropyContainerBlockEntity abe = (AbstractEntropyContainerBlockEntity) be;
                                // Do transfer
                                if (abe.canAcceptFromAltar()) {
                                    abe.addEntropy(1);
                                    addEntropy(-1);

                                    if (getEntropy() <= 0) {
                                        return;
                                    }
                                }
                            }
                        }
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
    }

    // Save values in compound tag
    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);

        tag.putInt("entropy",entropy);

        return tag;
    }

}
