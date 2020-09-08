package cookie04.digitalitems.common.tileentities;

import cookie04.digitalitems.Config;
import cookie04.digitalitems.Registration;
import cookie04.digitalitems.common.OwnEnergy;
import cookie04.digitalitems.common.peripherals.ItemDigitizerPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;

public class ItemDigitizerTileEntity extends TileEntity implements ITickableTileEntity {

    private final ItemStackHandler itemHandler = createHandler();

    private final OwnEnergy energyStorage = createEnergy();

    public LazyOptional<IEnergyStorage> energy = LazyOptional.of(() -> energyStorage);

    public LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new ItemDigitizerPeripheral(this));

    public ItemDigitizerTileEntity() {
        super(Registration.ITEM_DIGITIZER_TILE.get());
    }

    @Override
    public void remove() {
        super.remove();
        handler.invalidate();
        energy.invalidate();
    }

    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT tag) {
        super.read(state, tag);
        itemHandler.deserializeNBT(tag.getCompound("inv"));
        energyStorage.deserializeNBT(tag.getCompound("energy"));
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        tag.put("inv", itemHandler.serializeNBT());
        tag.put("energy", energyStorage.serializeNBT());
        return super.write(tag);
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(1) {

            @Override
            protected void onContentsChanged(int slot) {
                // To make sure the TE persists when the chunk is saved later we need to
                // mark it dirty every time the item handler changes
                markDirty();
            }

            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return true;
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return super.insertItem(slot, stack, simulate);
            }
        };
    }
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return handler.cast();
        }
        if(cap == Registration.PERIPHERAL_CAPABILITY) {
            return (LazyOptional<T>) peripheral;
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }

    private OwnEnergy createEnergy() {
        return new OwnEnergy(100, 0) {
            @Override
            protected void onEnergyChanged() {
                markDirty();
            }
        };
    }
    @Override
    public void tick() {
        assert world != null;
        if(world.isRemote) {
            return;
        }
        update();
    }
    public void update() {
        if(Config.ITEM_DIGITIZER_POWER_ENABLED.get()) {
            acceptPower();
        }
        ItemDigitizerTileEntity entity = (ItemDigitizerTileEntity) getTileEntity();
        assert world != null;
        BlockState state = world.getBlockState(pos);
        boolean powered = entity.energyStorage.getEnergyStored() > 0;
        if(state.get(BlockStateProperties.POWERED) != powered) {
            world.setBlockState(pos, state.with(BlockStateProperties.POWERED, powered));
        }
    }

    private void acceptPower() {
        AtomicInteger energy = new AtomicInteger(energyStorage.getEnergyStored());
        if(energyStorage.getMaxEnergyStored() <= energy.get()) {
            return;
        }
        for(Direction direction : Direction.values()) {
            assert world != null;
            TileEntity te = world.getTileEntity(pos.offset(direction));
            if(te == null) {
                continue;
            }
            LazyOptional<IEnergyStorage> lazyOptional =  te.getCapability(CapabilityEnergy.ENERGY, direction);
            if(!lazyOptional.isPresent()) {
                continue;
            }
            assert(lazyOptional.resolve().isPresent());
            IEnergyStorage externalEnergyStorage = lazyOptional.resolve().get();
            if(!externalEnergyStorage.canExtract()) {
                continue;
            }
            int extracted = externalEnergyStorage.extractEnergy(energyStorage.getMaxEnergyStored() - energy.get(), false);
            energyStorage.addEnergy(extracted);
            int energyNow = energyStorage.getEnergyStored();
            energy.set(energyNow);
            markDirty();
            if(energyNow >= energyStorage.getMaxEnergyStored()) {
                break;
            }
        }
    }
}
