package lazy.moofluids.client.screen;

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
        this.renderTooltip(matrixStack, mouseX, mouseY);
        if(this.hoveredSlot != null && !this.hoveredSlot.hasItem()) {
            if(this.hoveredSlot.index == 0) {
                this.renderTooltip(matrixStack, new StringTextComponent("Input (Empty Bucket)"), mouseX, mouseY);
            } else {
                this.renderTooltip(matrixStack, new StringTextComponent("Output"), mouseX, mouseY);
            }
        }

        if(mouseX > this.leftPos + 61 && mouseX < this.leftPos + 61 + 54 && mouseY > this.topPos + 14 && mouseY < this.topPos + 14 + 64) {
            Fluid fluidFromColor = FluidColorFromTexture.getFluidFromColor(this.menu.getFluidColor());
            String fluidName = new FluidStack(fluidFromColor, FluidAttributes.BUCKET_VOLUME).getDisplayName().getString();
            this.renderTooltip(matrixStack, new StringTextComponent(fluidName + ": " + this.menu.getFluidAmount() + "/" + this.menu.getCapacity()), mouseX, mouseY);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void renderBg(@Nonnull MatrixStack matrixStack, float partialTicks, int x, int y) {
        GlStateManager._color4f(1f, 1f, 1f, 1f);
        if(this.minecraft != null) {
            this.minecraft.getTextureManager().bind(BACKGROUND);
            int i = this.leftPos;
            int j = this.topPos;
            this.blit(matrixStack, i, j, 0, 0, this.width, this.height);

            if(!this.menu.isEmpty()) {
                this.setFluidAmount();
                int startY = this.topPos + 15 + 62 - (int) (this.fluidPercentage * 62);
                float[] rgba = this.convert(this.menu.getFluidColor());
                GlStateManager._color4f(rgba[0], rgba[1], rgba[2], rgba[3]);
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
