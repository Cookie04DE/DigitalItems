package cookie04.digitalitems.common;

import cookie04.digitalitems.DigitalItems;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

import java.util.function.Supplier;

public class NBTSaver extends WorldSavedData implements Supplier {
    public CompoundNBT data = new CompoundNBT();

    public NBTSaver()
    {
        super(DigitalItems.MOD_ID);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        data = nbt.getCompound("DigitalItems");
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.put("DigitalItems", data);
        return nbt;
    }

    public static NBTSaver get(ServerWorld world)
    {
        return (NBTSaver) world.getSavedData().getOrCreate(new NBTSaver(), DigitalItems.MOD_ID);
    }

    @Override
    public Object get()
    {
        return this;
    }
}
