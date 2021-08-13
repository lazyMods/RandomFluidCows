package lazy.moofluids.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import lazy.moofluids.MooFluids;
import lazy.moofluids.inventory.container.AutoMilkerContainer;
import lazy.moofluids.utils.FluidColorFromTexture;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class AutoMilkerScreen extends AbstractContainerScreen<AutoMilkerContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(MooFluids.MOD_ID, "textures/gui/auto_milker.png");

    private int fluidAmount;
    private float fluidPercentage;

    public AutoMilkerScreen(AutoMilkerContainer screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(@Nonnull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
        if (this.hoveredSlot != null && !this.hoveredSlot.hasItem()) {
            if (this.hoveredSlot.index == 0) {
                this.renderTooltip(matrixStack, new TextComponent("Input (Empty Bucket)"), mouseX, mouseY);
            } else {
                this.renderTooltip(matrixStack, new TextComponent("Output"), mouseX, mouseY);
            }
        }

        if (mouseX > this.leftPos + 61 && mouseX < this.leftPos + 61 + 54 && mouseY > this.topPos + 14 && mouseY < this.topPos + 14 + 64) {
            Fluid fluidFromColor = FluidColorFromTexture.getFluidFromColor(this.menu.getFluidColor());
            String fluidName = new FluidStack(fluidFromColor, FluidAttributes.BUCKET_VOLUME).getDisplayName().getString();
            this.renderTooltip(matrixStack, new TextComponent(fluidName + ": " + this.menu.getFluidAmount() + "/" + this.menu.getCapacity()), mouseX, mouseY);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, BACKGROUND);
        if (this.minecraft != null) {
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrixStack, i, j, 0, 0, this.imageWidth, this.imageHeight);

            if (!this.menu.isEmpty()) {
                this.setFluidAmount();
                int startY = this.topPos + 15 + 62 - (int) (this.fluidPercentage * 62);
                float[] rgba = this.convert(this.menu.getFluidColor());
                RenderSystem.setShaderColor(rgba[0], rgba[1], rgba[2], rgba[3]);
                this.blit(matrixStack, this.leftPos + 62, startY, 176, 0, 52, (int) (this.fluidPercentage * 62));
            }
        }
    }

    public void setFluidAmount() {
        this.fluidAmount = this.menu.getFluidAmount();
        this.fluidPercentage = this.fluidAmount / (float) this.menu.getCapacity();
    }

    private float[] convert(int color) {
        float a = ((color >> 24) & 0xFF) / 255f; // alpha
        float r = ((color >> 16) & 0xFF) / 255f; // red
        float g = ((color >> 8) & 0xFF) / 255f; // green
        float b = (color & 0xFF) / 255f; // blue
        return new float[]{r, g, b, a};
    }
}
