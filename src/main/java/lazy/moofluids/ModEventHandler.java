package lazy.moofluids;

import lazy.moofluids.client.model.MooFluidModel;
import lazy.moofluids.client.model.item.UniversalBucketModel;
import lazy.moofluids.client.render.MooFluidRenderer;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.utils.FluidColorFromTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MooFluids.MOD_ID, value = Dist.CLIENT)
public class ModEventHandler {

    public static boolean donePopulation = false;

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onTextureStitching(TextureStitchEvent.Pre e) {
        if (!e.getAtlas().location().equals(TextureAtlas.LOCATION_BLOCKS)) return;
        e.addSprite(new ResourceLocation(MooFluids.MOD_ID, "item/bucket_fluid_drip"));
    }

    @SubscribeEvent
    public static void onTexturePostStitch(TextureStitchEvent.Post event) {
        if (donePopulation) return;
        FluidColorFromTexture.populate();
        donePopulation = true;
    }

    public static final ModelResourceLocation INVENTORY_MODEL = new ModelResourceLocation(MooFluids.MOD_ID.concat(":universal_bucket"), "inventory");

    @SubscribeEvent
    public static void onModelBakeEvent(ModelBakeEvent event) {
        BakedModel iBakedModel = event.getModelRegistry().get(INVENTORY_MODEL);
        UniversalBucketModel dynamicBucketModel = new UniversalBucketModel(iBakedModel);
        event.getModelRegistry().put(INVENTORY_MODEL, dynamicBucketModel);
    }

    @SubscribeEvent
    public static void onEntityAttributeRegister(EntityAttributeCreationEvent event) {
        event.put(Setup.MOO_FLUID.get(), MooFluidEntity.createAttr());
    }

    @SubscribeEvent
    public static void onEntityRenderingRegisterEvent(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(Setup.MOO_FLUID.get(), MooFluidRenderer::new);
    }

    @SubscribeEvent
    public static void onEntityLayerDefinition(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(MooFluidModel.LAYER, MooFluidModel::createBodyLayer);
    }
}
