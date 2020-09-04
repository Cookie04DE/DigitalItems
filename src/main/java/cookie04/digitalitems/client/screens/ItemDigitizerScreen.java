package cookie04.digitalitems.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import cookie04.digitalitems.DigitalItems;
import cookie04.digitalitems.common.container.ItemDigitizerContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;

public class ItemDigitizerScreen extends ContainerScreen<ItemDigitizerContainer> {
    private final ResourceLocation GUI = new ResourceLocation(DigitalItems.MOD_ID, "textures/gui/item_digitizer_container.png");

    public ItemDigitizerScreen(ItemDigitizerContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
    }

    @Override
    public void render(@Nonnull MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(stack, mouseX, mouseY);
    }


    protected void drawGuiContainerForegroundLayer(@Nonnull MatrixStack stack, int mouseX, int mouseY) {
        drawString(stack, Minecraft.getInstance().fontRenderer, "Energy: " + container.getEnergy(), 10, 10, 0xffffff);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(GUI);
        int relX = (this.width - this.xSize) / 2;
        int relY = (this.height - this.ySize) / 2;
        this.blit(stack, relX, relY, 0, 0, this.xSize, this.ySize);
    }
}
