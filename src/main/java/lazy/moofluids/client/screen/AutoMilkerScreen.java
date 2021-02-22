package lazy.moofluids.client.screen;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import lazy.moofluids.MooFluids;
import lazy.moofluids.inventory.container.AutoMilkerContainer;
import lazy.moofluids.utils.FluidColorFromTexture;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;

public class AutoMilkerScreen extends ContainerScreen<AutoMilkerContainer> {

    public static final ResourceLocation BACKGROUND = new ResourceLocation(MooFluids.MOD_ID, "textures/gui/auto_milker.png");

    private int fluidAmount;
    private float fluidPercentage;

    public AutoMilkerScreen(AutoMilkerContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
        if (this.hoveredSlot != null && !this.hoveredSlot.getHasStack()) {
            if (this.hoveredSlot.slotNumber == 0) {
                this.renderTooltip(matrixStack, new StringTextComponent("Input (Empty Bucket)"), mouseX, mouseY);
            } else {
                this.renderTooltip(matrixStack, new StringTextComponent("Output"), mouseX, mouseY);
            }
        }

        if (mouseX > this.guiLeft + 61 && mouseX < this.guiLeft + 61 + 54 && mouseY > this.guiTop + 14 && mouseY < this.guiTop + 14 + 64) {
            Fluid fluidFromColor = FluidColorFromTexture.getFluidFromColor(this.container.getFluidColor());
            String fluidName = new FluidStack(fluidFromColor, FluidAttributes.BUCKET_VOLUME).getDisplayName().getString();
            this.renderTooltip(matrixStack, new StringTextComponent(fluidName + ": " + this.container.getFluidAmount() + "/" + this.container.getCapacity()), mouseX, mouseY);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void drawGuiContainerBackgroundLayer(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        GlStateManager.color4f(1f, 1f, 1f, 1f);
        if (this.minecraft != null) {
            this.minecraft.getTextureManager().bindTexture(BACKGROUND);
            int i = this.guiLeft;
            int j = this.guiTop;
            this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);

            if (!this.container.isEmpty()) {
                this.setFluidAmount();
                int startY = this.guiTop + 15 + 62 - (int) (this.fluidPercentage * 62);
                float[] rgba = this.convert(this.container.getFluidColor());
                GlStateManager.color4f(rgba[0], rgba[1], rgba[2], rgba[3]);
                this.blit(matrixStack, this.guiLeft + 62, startY, 176, 0, 52, (int) (this.fluidPercentage * 62));
            }
        }
    }

    public void setFluidAmount() {
        this.fluidAmount = this.container.getFluidAmount();
        this.fluidPercentage = this.fluidAmount / (float) this.container.getCapacity();
    }

    private float[] convert(int color) {
        float a = ((color >> 24) & 0xFF) / 255f; // alpha
        float r = ((color >> 16) & 0xFF) / 255f; // red
        float g = ((color >> 8) & 0xFF) / 255f; // green
        float b = (color & 0xFF) / 255f; // blue
        return new float[]{r, g, b, a};
    }
}
