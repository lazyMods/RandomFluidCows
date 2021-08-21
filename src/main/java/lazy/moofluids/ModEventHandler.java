package lazy.moofluids;

import lazy.moofluids.client.model.item.UniversalBucketModel;
import lazy.moofluids.item.UniversalBucketItem;
import lazy.moofluids.utils.FluidColorFromTexture;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.client.model.DynamicBucketModel;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MooFluids.MOD_ID, value = Dist.CLIENT)
public class ModEventHandler {

    public static boolean donePopulation = false;

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onTextureStitching(TextureStitchEvent.Pre e) {
        if (!e.getMap().location().equals(AtlasTexture.LOCATION_BLOCKS)) return;
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
        IBakedModel iBakedModel = event.getModelRegistry().get(INVENTORY_MODEL);
        UniversalBucketModel dynamicBucketModel = new UniversalBucketModel(iBakedModel);
        event.getModelRegistry().put(INVENTORY_MODEL, dynamicBucketModel);
    }
}
