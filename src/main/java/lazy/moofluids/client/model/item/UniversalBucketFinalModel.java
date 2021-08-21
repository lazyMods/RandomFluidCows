package lazy.moofluids.client.model.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.ItemTextureQuadConverter;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings({"NullableProblems", "deprecation", "ConstantConditions"})
public class UniversalBucketFinalModel implements IBakedModel {

    private final IBakedModel givenModel;
    private final Fluid fluid;

    private static final float NORTH_Z_FLUID = 7.498f / 16f;
    private static final float SOUTH_Z_FLUID = 8.502f / 16f;

    public UniversalBucketFinalModel(IBakedModel givenModel, Fluid fluid) {
        this.givenModel = givenModel;
        this.fluid = fluid;
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(BlockState state, Direction side, Random rand) {
        TextureAtlasSprite bucket = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(new ResourceLocation("minecraft:item/bucket"));
        TextureAtlasSprite fluidMask = Minecraft.getInstance().getTextureAtlas(AtlasTexture.LOCATION_BLOCKS).apply(new ResourceLocation("moofluids:item/bucket_fluid_drip"));
        TextureAtlasSprite fluidSprite = fluid != Fluids.EMPTY ? ForgeHooksClient.getBlockMaterial(fluid.getAttributes().getStillTexture()).sprite() : null;

        if (side != null) return this.givenModel.getQuads(state, side, rand);

        List<BakedQuad> quads = new ArrayList<>(this.givenModel.getQuads(state, null, rand));
        quads.addAll(ItemLayerModel.getQuadsForSprite(0, bucket, TransformationMatrix.identity()));
        if (fluidMask != null && fluidSprite != null) {


            int color = fluid.getAttributes().getColor();
            quads.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), fluidMask, fluidSprite, NORTH_Z_FLUID, Direction.NORTH, color, 1, 1));
            quads.addAll(ItemTextureQuadConverter.convertTexture(TransformationMatrix.identity(), fluidMask, fluidSprite, SOUTH_Z_FLUID, Direction.SOUTH, color, 1, 1));

        }
        return quads;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.givenModel.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.givenModel.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }


    @Override
    public boolean isCustomRenderer() {
        return this.givenModel.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return ModelLoader.instance().getSpriteMap().getAtlas(AtlasTexture.LOCATION_BLOCKS).getSprite(new ResourceLocation("minecraft:diamond_block"));
    }


    @Override
    public TextureAtlasSprite getParticleTexture(@Nonnull IModelData data) {
        return ModelLoader.instance().getSpriteMap().getAtlas(AtlasTexture.LOCATION_BLOCKS).getSprite(new ResourceLocation("minecraft:diamond_block"));
    }

    @Override
    public ItemCameraTransforms getTransforms() {
        return this.givenModel.getTransforms();
    }

    @Override
    public ItemOverrideList getOverrides() {
        throw new UnsupportedOperationException("OH NO  OH NO OH NO NONONONONOO");
    }
}