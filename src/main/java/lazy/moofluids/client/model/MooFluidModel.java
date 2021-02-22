package lazy.moofluids.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.entity.model.QuadrupedModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class MooFluidModel<T extends Entity> extends QuadrupedModel<T> {

    private float redTint = 1.0F;
    private float greenTint = 1.0F;
    private float blueTint = 1.0F;
    private float alphaTint = 1.0F;

    public MooFluidModel() {
        super(12, 0.0F, false, 10.0F, 4.0F, 2.0F, 2.0F, 24);
        this.headModel = new ModelRenderer(this, 0, 0);
        this.headModel.addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 6.0F, 0.0F);
        this.headModel.setRotationPoint(0.0F, 4.0F, -8.0F);
        this.headModel.setTextureOffset(22, 0).addBox(-5.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F, 0.0F);
        this.headModel.setTextureOffset(22, 0).addBox(4.0F, -5.0F, -4.0F, 1.0F, 3.0F, 1.0F, 0.0F);
        this.body = new ModelRenderer(this, 18, 4);
        this.body.addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, 0.0F);
        this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
        this.body.setTextureOffset(52, 0).addBox(-2.0F, 2.0F, -8.0F, 4.0F, 6.0F, 1.0F);
        --this.legBackRight.rotationPointX;
        ++this.legBackLeft.rotationPointX;
        this.legBackRight.rotationPointZ += 0.0F;
        this.legBackLeft.rotationPointZ += 0.0F;
        --this.legFrontRight.rotationPointX;
        ++this.legFrontLeft.rotationPointX;
        --this.legFrontRight.rotationPointZ;
        --this.legFrontLeft.rotationPointZ;
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, this.redTint * red, this.greenTint * green, this.blueTint * blue, this.alphaTint * alpha);
    }

    public void setTint(float r, float g, float b, float a) {
        this.redTint = r;
        this.greenTint = g;
        this.blueTint = b;
        this.alphaTint = a;
    }
}
