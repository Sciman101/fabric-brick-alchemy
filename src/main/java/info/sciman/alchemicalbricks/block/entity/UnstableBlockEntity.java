package info.sciman.alchemicalbricks.block.entity;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;

public class UnstableBlockEntity extends AbstractEntropyContainerBlockEntity {
    public UnstableBlockEntity() {
        super(AlchemicalBricksMod.UNSTABLE_BLOCK_ENTITY);
    }

    @Override
    public int getEntropyCapacity() {
        return 250;
    }

    @Override
    void onEntropyOverflow() {
        // do nothing for now
    }

    @Override
    public boolean canAcceptFromAltar() {
        return true;
    }
}
