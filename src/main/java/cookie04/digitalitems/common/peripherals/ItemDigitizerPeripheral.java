package cookie04.digitalitems.common.peripherals;

import cookie04.digitalitems.Config;
import cookie04.digitalitems.DigitalItems;
import cookie04.digitalitems.common.luameta.LuaItem;
import cookie04.digitalitems.common.tileentities.ItemDigitizerTileEntity;
import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;

public class ItemDigitizerPeripheral implements IDynamicPeripheral {
    private final ItemDigitizerTileEntity tileEntity;
    public ItemDigitizerPeripheral(ItemDigitizerTileEntity te) {
        this.tileEntity = te;
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

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return new String[] {"digitize", "rematerialize", "check", "checkID", "getEnergy"};
    }

    @Nonnull
    @Override
    public MethodResult callMethod(@Nonnull IComputerAccess iComputerAccess, @Nonnull ILuaContext iLuaContext, int i, @Nonnull IArguments iArguments) throws LuaException {
        assert(tileEntity.energy.resolve().isPresent() && tileEntity.handler.resolve().isPresent());
        IEnergyStorage energy = tileEntity.energy.resolve().get();
        IItemHandler handler = tileEntity.handler.resolve().get();
        boolean powerEnabled = Config.ITEM_DIGITIZER_POWER_ENABLED.get();
        switch(i) {
            case 0:
                if(iArguments.count() > 0) {
                    return MethodResult.of(null, "Too many arguments");
                }
                ItemStack itemStack = handler.getStackInSlot(0);
                if(itemStack.isEmpty()) {
                    return MethodResult.of(null, "Item digitizer is empty");
                }
                if(powerEnabled) {
                    int digitizeCost = Config.ITEM_DIGITIZER_DIGITIZE_COST.get();
                    if(energy.extractEnergy(digitizeCost, true) != digitizeCost) {
                        return MethodResult.of(null, "Not enough energy to digitize, requires at least: " + digitizeCost);
                    } else {
                        energy.extractEnergy(digitizeCost, false);
                    }
                }
                Integer random = DigitalItems.random.nextInt();
                DigitalItems.digital_items.put(random, itemStack.serializeNBT());
                handler.extractItem(0, itemStack.getCount(), false);
                return MethodResult.of(random, null);
            case 1:
                if(iArguments.count() != 1) {
                    return MethodResult.of("Expected one argument: item number, got: " + iArguments.count());
                }
                if(!(iArguments.get(0) instanceof Double)) {
                    return MethodResult.of("Expected first argument to be the item number(an number)");
                }
                double number = iArguments.getDouble(0);
                if(Math.floor(number) != number) {
                    return MethodResult.of("Expected item number to be whole");
                }
                int intNumber = (int) number;
                if(!DigitalItems.digital_items.containsKey(intNumber)) {
                    return MethodResult.of("Invalid item number");
                }
                if(powerEnabled) {
                    int rematerializeCost = Config.ITEM_DIGITIZER_REMATERIALISE_COST.get();
                    if(energy.extractEnergy(rematerializeCost, true) != rematerializeCost) {
                        return MethodResult.of("Not enough energy to rematerialize, requires at least: " + rematerializeCost);
                    } else {
                        energy.extractEnergy(rematerializeCost, false);
                    }
                }
                CompoundNBT nbt = DigitalItems.digital_items.get(intNumber);
                ItemStack insert = handler.insertItem(0, ItemStack.read(nbt), true);
                if(insert.getCount() != 0) {
                    return MethodResult.of("Digitizer not empty and can't merge items");
                }
                handler.insertItem(0, ItemStack.read(nbt), false);
                DigitalItems.digital_items.remove(intNumber);
                return MethodResult.of((Object) null);
            case 2:
                if(iArguments.count() > 0) {
                    return MethodResult.of(null, "Too many arguments");
                }
                if(powerEnabled) {
                    int checkCost = Config.ITEM_DIGITIZER_REMATERIALISE_COST.get();
                    if(energy.extractEnergy(checkCost, true) != checkCost) {
                        return MethodResult.of(null, "Not enough energy to check, requires at least: " + checkCost);
                    } else {
                        energy.extractEnergy(checkCost, false);
                    }
                }
                ItemStack stack = handler.getStackInSlot(0);
                if(stack.isEmpty()) {
                    return MethodResult.of(null, null);
                }
                return MethodResult.of(LuaItem.get(stack), null);
            case 3:
                if(iArguments.count() != 1) {
                    return MethodResult.of(null, "Expected one argument: item number, got: " + iArguments.count());
                }
                if(!(iArguments.get(0) instanceof Double)) {
                    return MethodResult.of("Expected first argument to be the item number(an number)");
                }
                number = iArguments.getDouble(0);
                if(Math.floor(number) != number) {
                    return MethodResult.of("Expected item number to be whole");
                }
                intNumber = (int) number;
                if(!DigitalItems.digital_items.containsKey(intNumber)) {
                    return MethodResult.of("Invalid item number");
                }
                if(powerEnabled) {
                    int checkCost = Config.ITEM_DIGITIZER_CHECK_COST.get();
                    if(energy.extractEnergy(checkCost, true) != checkCost) {
                        return MethodResult.of(null, "Not enough energy to check, requires at least: " + checkCost);
                    } else {
                        energy.extractEnergy(checkCost, false);
                    }
                }
                return MethodResult.of(LuaItem.get(ItemStack.read(DigitalItems.digital_items.get(intNumber))), null);
            case 4:
                if(powerEnabled) {
                    return MethodResult.of(energy.getEnergyStored(), null);
                } else {
                    return MethodResult.of(null, "Energy consumption is turned off");
                }
        }
        return MethodResult.of();
    }
}
