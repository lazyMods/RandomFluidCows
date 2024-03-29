package lazy.moofluids.client.model.item;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Random;

public class UniversalBucketModel implements IBakedModel {

    private final IBakedModel givenModel;
    private final UniversalBucketOverrides universalBucketOverrides;

    public UniversalBucketModel(IBakedModel givenModel) {
        this.givenModel = givenModel;
        this.universalBucketOverrides = new UniversalBucketOverrides();
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(BlockState state, Direction side, @Nonnull Random rand) {
        return this.givenModel.getQuads(state, side, rand);
    }

    @Override
    @Nonnull
    public List<BakedQuad> getQuads(BlockState state, Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        throw new AssertionError("OH NO  OH NO OH NO NONONONONOO");
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        throw new AssertionError("OH NO  OH NO OH NO NONONONONOO");
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
        return this.givenModel.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.givenModel.isCustomRenderer();
    }

    @SuppressWarnings("NullableProblems")
    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.givenModel.getParticleIcon();
    }

    @Override
    public ItemOverrideList getOverrides() {
        return this.universalBucketOverrides;
    }
}
