package cookie04.digitalitems;

import cookie04.digitalitems.client.SetupClient;
import cookie04.digitalitems.common.NBTSaver;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IWorld;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

@Mod(DigitalItems.MOD_ID)
public class DigitalItems {
    public static final String MOD_ID = "digitalitems";
    public static HashMap<Integer, CompoundNBT> digital_items = new HashMap<>();
    public static final SecureRandom random = new SecureRandom();
    public DigitalItems() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.SERVER_CONFIG);
        Registration.register();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(SetupClient::init);
        ComputerCraftAPI.registerPeripheralProvider(((world, blockPos, direction) -> {
            TileEntity te = world.getTileEntity(blockPos);
            if(te == null) {
                return LazyOptional.empty();
            }
            LazyOptional<IPeripheral> capabilityLazyOptional = te.getCapability(Registration.PERIPHERAL_CAPABILITY);
            if(capabilityLazyOptional.isPresent()){
                return capabilityLazyOptional;
            }
            return LazyOptional.empty();
        }));
        MinecraftForge.EVENT_BUS.addListener(this::onWorldLoaded);
        MinecraftForge.EVENT_BUS.addListener(this::onWorldSaved);
    }

    public void onWorldLoaded(WorldEvent.Load event) {
        IWorld world = event.getWorld();
        if(world.isRemote() || !(world instanceof ServerWorld)) {
            return;
        }
        digital_items = new HashMap<>();
        NBTSaver saver = NBTSaver.get((ServerWorld)world);
        if(!saver.data.contains("items")) {
            return;
        }
        CompoundNBT items = saver.data.getCompound("items");
        for(String key : items.keySet()) {
            digital_items.put(Integer.parseInt(key), items.getCompound(key));
        }
    }

    public void onWorldSaved(WorldEvent.Save event) {
        IWorld world = event.getWorld();
        if(world.isRemote() || !(world instanceof ServerWorld)) {
            return;
        }
        NBTSaver saver = NBTSaver.get((ServerWorld)world);
        CompoundNBT data = new CompoundNBT();
        CompoundNBT items = new CompoundNBT();
        for(Map.Entry<Integer, CompoundNBT> entry : digital_items.entrySet()) {
            items.put(entry.getKey().toString(), entry.getValue());
        }
        data.put("items", items);
        saver.data = data;
        saver.markDirty();
    }

    public static int doubleWholeCheck(double d, String errorMsg) throws LuaException {
        if(Math.floor(d) == d) {
            return (int)d;
        }
        throw new LuaException(errorMsg);
    }
}
