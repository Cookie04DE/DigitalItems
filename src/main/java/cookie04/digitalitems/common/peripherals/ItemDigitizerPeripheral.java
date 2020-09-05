package cookie04.digitalitems.common.peripherals;

import cookie04.digitalitems.Config;
import cookie04.digitalitems.DigitalItems;
import cookie04.digitalitems.common.luameta.LuaItem;
import cookie04.digitalitems.common.tileentities.ItemDigitizerTileEntity;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.LuaFunction;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import java.util.Map;

public class ItemDigitizerPeripheral implements IPeripheral {
    private final ItemDigitizerTileEntity tileEntity;
    public ItemDigitizerPeripheral(ItemDigitizerTileEntity te) {
        this.tileEntity = te;
    }

    IEnergyStorage energy;
    IItemHandler handler;
    ItemStack itemStack;
    boolean powerEnabled;

    private void updateInfo() {
        assert(tileEntity.energy.resolve().isPresent() && tileEntity.handler.resolve().isPresent());
        energy = tileEntity.energy.resolve().get();
        handler = tileEntity.handler.resolve().get();
        itemStack = handler.getStackInSlot(0);
        powerEnabled = Config.ITEM_DIGITIZER_POWER_ENABLED.get();
    }
    @Nonnull
    @Override
    public String getType() {
        return "item_digitizer";
    }

    @Override
    public void attach(@Nonnull IComputerAccess computer) {
    }

    @Override
    public void detach(@Nonnull IComputerAccess computer) {
    }

    @Override
    public boolean equals(IPeripheral iPeripheral) {
        return iPeripheral == this;
    }

    /**
     * Digitizes an item.
     *
     * @return The id of the item that was digitized
     * @throws LuaException If theres no item in the digitizer or there's not enough power
     */
    @LuaFunction
    public final double digitize() throws LuaException{
        updateInfo();
        if(itemStack.isEmpty()) {
            throw new LuaException("No item to digitize");
        }
        if(powerEnabled) {
            int digitizeCost = Config.ITEM_DIGITIZER_DIGITIZE_COST.get();
            if(energy.extractEnergy(digitizeCost, true) != digitizeCost) {
                throw new LuaException("Not enough energy to digitize, requires at least: " + digitizeCost);
            } else {
                energy.extractEnergy(digitizeCost, false);
            }
        }
        int random = DigitalItems.random.nextInt();
        DigitalItems.digital_items.put(random, itemStack.serializeNBT());
        handler.extractItem(0, itemStack.getCount(), false);
        return random;
    }

    /**
     * Rematerializes an item.
     *
     * @param itemID The ID of the item to be rematerialized.
     * @throws LuaException If the ID is not a whole number or invalid or the digitizer is not empty and the two item stacks can't be merged.
     */
    @LuaFunction
    public final void rematerialize(double itemID) throws LuaException {
        updateInfo();
        int intID = DigitalItems.doubleWholeCheck(itemID, "Expected the itemID to be a whole number");
        if(!DigitalItems.digital_items.containsKey(intID)) {
            throw new LuaException("Invalid itemID");
        }
        if(powerEnabled) {
            int rematerializeCost = Config.ITEM_DIGITIZER_REMATERIALISE_COST.get();
            if(energy.extractEnergy(rematerializeCost, true) != rematerializeCost) {
                throw new LuaException("Not enough energy to rematerialize, requires at least: " + rematerializeCost);
            } else {
                energy.extractEnergy(rematerializeCost, false);
            }
        }
        CompoundNBT nbt = DigitalItems.digital_items.get(intID);
        ItemStack insert = handler.insertItem(0, ItemStack.read(nbt), true);
        if(insert.getCount() != 0) {
            throw new LuaException("Digitizer not empty and can't merge items");
        }
        handler.insertItem(0, ItemStack.read(nbt), false);
        DigitalItems.digital_items.remove(intID);
    }

    /**
     * Gets information about the item stack currently in the digitizer.
     *
     * @return The table containing the information about the item stack or null, if theres no item stack there.
     * @throws LuaException If there is not enough energy to get the item info.
     */
    @LuaFunction
    public final Map<String, Object> data() throws LuaException {
        ItemStack stack = handler.getStackInSlot(0);
        if(stack.isEmpty()) {
            return null;
        }
        if(powerEnabled) {
            int checkCost = Config.ITEM_DIGITIZER_REMATERIALISE_COST.get();
            if(energy.extractEnergy(checkCost, true) != checkCost) {
                throw new LuaException("Not enough energy to check, requires at least: " + checkCost);
            } else {
                energy.extractEnergy(checkCost, false);
            }
        }
        return LuaItem.get(stack);
    }

    /**
     * Gets information about an item stack from it's itemID.
     * @param itemID The itemID
     * @return The table containing the information about the item stack.
     * @throws LuaException If the itemID is not a whole number or invalid.
     */
    @LuaFunction
    public final Map<String, Object> dataID(double itemID) throws LuaException {
        int intID = DigitalItems.doubleWholeCheck(itemID, "Expected the itemID to be a whole number");
        if(!DigitalItems.digital_items.containsKey(intID)) {
            throw new LuaException("Invalid itemID");
        }
        if(powerEnabled) {
            int checkCost = Config.ITEM_DIGITIZER_CHECK_COST.get();
            if(energy.extractEnergy(checkCost, true) != checkCost) {
                throw new LuaException("Not enough energy to check, requires at least: " + checkCost);
            } else {
                energy.extractEnergy(checkCost, false);
            }
        }
        return LuaItem.get(ItemStack.read(DigitalItems.digital_items.get(intID)));
    }
}
