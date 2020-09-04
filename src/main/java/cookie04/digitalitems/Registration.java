package cookie04.digitalitems;

import cookie04.digitalitems.common.blocks.ItemDigitizerBlock;
import cookie04.digitalitems.common.container.ItemDigitizerContainer;
import cookie04.digitalitems.common.tileentities.ItemDigitizerTileEntity;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Registration {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, DigitalItems.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, DigitalItems.MOD_ID);
    private static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, DigitalItems.MOD_ID);
    private static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, DigitalItems.MOD_ID);

    public static final RegistryObject<ItemDigitizerBlock> ITEM_DIGITIZER_BLOCK = BLOCKS.register("item_digitizer", ItemDigitizerBlock::new);
    public static final RegistryObject<Item> ITEM_DIGITIZER_BLOCK_ITEM = ITEMS.register("item_digitizer", () -> new BlockItem(ITEM_DIGITIZER_BLOCK.get(), new Item.Properties().group(ItemGroup.BUILDING_BLOCKS)));
    public static final RegistryObject<TileEntityType<ItemDigitizerTileEntity>> ITEM_DIGITIZER_TILE = TILES.register("item_digitizer", () -> TileEntityType.Builder.create(ItemDigitizerTileEntity::new, ITEM_DIGITIZER_BLOCK.get()).build(null));
    public static final RegistryObject<ContainerType<ItemDigitizerContainer>> ITEM_DIGITIZER_CONTAINER = CONTAINERS.register("item_digitizer", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        World world = inv.player.getEntityWorld();
        return new ItemDigitizerContainer(windowId, world, pos, inv, inv.player);
    }));

    @CapabilityInject(IPeripheral.class)
    public static Capability<IPeripheral> PERIPHERAL_CAPABILITY = null;

    public static void register() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        TILES.register(bus);
        CONTAINERS.register(bus);
    }
}
