package lazy.moofluids.client.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import lazy.moofluids.MooFluids;
import lazy.moofluids.client.model.MooFluidModel;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.utils.FluidColorFromTexture;
import lazy.moofluids.utils.ImageDominantColor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

@OnlyIn(Dist.CLIENT)
public class MooFluidRenderer extends MobRenderer<MooFluidEntity, MooFluidModel<MooFluidEntity>> {

    private static final ResourceLocation COW_TEXTURES = new ResourceLocation("textures/entity/cow/cow.png");

    public MooFluidRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new MooFluidModel<>(), 0.7F);
    }

    @Override
    public void render(MooFluidEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        float[] rgba = this.convert(this.getColorFromFluid(entityIn.getFluid()));
        this.entityModel.setTint(rgba[0], rgba[1], rgba[2], rgba[3]);
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    /**
     * Returns the location of an entity's texture.
     */
    public ResourceLocation getEntityTexture(MooFluidEntity entity) {
        return COW_TEXTURES;
    }

    private int getColorFromFluid(Fluid fluid){
        if(fluid == null || fluid == Fluids.EMPTY) return 0xFFFFFFFF;
        if(fluid.getAttributes().getColor() != -1) return fluid.getAttributes().getColor();
        return FluidColorFromTexture.COLORS.get(fluid);
    }

    private float[] convert(int color) {
        float a = ((color >> 24) & 0xFF) / 255f; // alpha
        float r = ((color >> 16) & 0xFF) / 255f; // red
        float g = ((color >> 8) & 0xFF) / 255f; // green
        float b = (color & 0xFF) / 255f; // blue
        return new float[]{r, g, b, a};
    }
}

