package cookie04.digitalitems.common.blocks;

import cookie04.digitalitems.Registration;
import cookie04.digitalitems.common.container.ItemDigitizerContainer;
import cookie04.digitalitems.common.tileentities.ItemDigitizerTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.Optional;

public class ItemDigitizerBlock extends Block {
    public ItemDigitizerBlock() {
        super(
                Properties.create(Material.IRON)
                .sound(SoundType.METAL)
                .hardnessAndResistance(2.0f)
                .setRequiresTool()
                .harvestTool(ToolType.PICKAXE)
                .harvestLevel(1));
    }

    @Nonnull
    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult trace) {
        if (!world.isRemote) {
            TileEntity tileEntity = world.getTileEntity(pos);
            if (tileEntity instanceof ItemDigitizerTileEntity) {
                INamedContainerProvider containerProvider = new INamedContainerProvider() {
                    @Override
                    public ITextComponent getDisplayName() {
                        return new TranslationTextComponent("screen.digitalitems.item_digitizer");
                    }

                    @Override
                    public Container createMenu(int i, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
                        return new ItemDigitizerContainer(i, world, pos, playerInventory, playerEntity);
                    }
                };
                NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getPos());
            } else {
                throw new IllegalStateException("Our named container provider is missing!");
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader reader) {
        return new ItemDigitizerTileEntity();
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return getDefaultState().with(BlockStateProperties.FACING, Objects.requireNonNull(context.getPlayer()).getHorizontalFacing().getOpposite());
    }

    @Override
    public void onBlockPlacedBy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, LivingEntity placer, @Nonnull ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if(tileEntity instanceof ItemDigitizerTileEntity) {
            ItemDigitizerTileEntity itemDigitizerTileEntity = (ItemDigitizerTileEntity) tileEntity;
            itemDigitizerTileEntity.update();
        }
    }

    /*@Override
    @SuppressWarnings("deprecated")
    public void onReplaced(BlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.isIn(newState.getBlock())) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof ItemDigitizerTileEntity) {
                Optional<IItemHandler> optional = ((ItemDigitizerTileEntity)tileentity).handler.resolve();
                assert optional.isPresent();
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), optional.get().getStackInSlot(0));
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), Registration.ITEM_DIGITIZER_BLOCK_ITEM.get().getDefaultInstance());
            }
        }
    }*/

    @Override
    public void onBlockHarvested(World worldIn, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity player) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof ItemDigitizerTileEntity) {
            Optional<IItemHandler> optional = ((ItemDigitizerTileEntity)tileentity).handler.resolve();
            assert optional.isPresent();
            InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), optional.get().getStackInSlot(0));
            if(!player.isCreative())
                InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), Registration.ITEM_DIGITIZER_BLOCK_ITEM.get().getDefaultInstance());
        }
    }
}
