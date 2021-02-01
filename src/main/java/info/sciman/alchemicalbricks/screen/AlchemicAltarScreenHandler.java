package info.sciman.alchemicalbricks.screen;

import info.sciman.alchemicalbricks.AlchemicalBricksMod;
import info.sciman.alchemicalbricks.block.entity.AlchemicAltarBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.FurnaceOutputSlot;
import net.minecraft.screen.slot.Slot;

public class AlchemicAltarScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    // Client only screenhandler constructor
    public AlchemicAltarScreenHandler(int syncId, PlayerInventory inv) {
        this(syncId,inv,new SimpleInventory(3),new ArrayPropertyDelegate(2));
    }

    public AlchemicAltarScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(AlchemicalBricksMod.ALCHEMICAL_WORKBENCH_SCREEN_HANDLER,syncId);
        checkSize(inventory,3);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;

        // Handle custom inventory logic, just in case
        inventory.onOpen(playerInventory.player);

        // Place slots in the correct location
        int m, l;
        this.addSlot(new Slot(inventory,0,50,41));
        this.addSlot(new FurnaceOutputSlot(playerInventory.player,inventory,1,110,42));

        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 84 + m * 18));
            }
        }
        //The player Hotbar
        for (m = 0; m < 9; ++m) {
            this.addSlot(new Slot(playerInventory, m, 8 + m * 18, 142));
        }

        // Add properties
        this.addProperties(propertyDelegate);
    }


    // Getters for the screen
    public float getEntropyPercentage() {
        return ((float)propertyDelegate.get(0)) / 100;
    }
    public int getConversionProgress() {
        return propertyDelegate.get(1);
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack transferSlot(PlayerEntity player, int index) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (index < this.inventory.size()) { // Trying to move out of our inventory
                if (!this.insertItem(originalStack,this.inventory.size(),this.slots.size(),true)) {
                    return ItemStack.EMPTY;
                }
            }else if (!this.insertItem(originalStack,0,this.inventory.size(),false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            }else{
                slot.markDirty();
            }
        }

        return newStack;
    }
}
