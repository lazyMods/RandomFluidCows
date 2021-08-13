package lazy.moofluids.client;

import lazy.moofluids.MooFluids;
import lazy.moofluids.Setup;
import lazy.moofluids.client.model.MooFluidModel;
import lazy.moofluids.client.render.MooFluidRenderer;
import lazy.moofluids.utils.FluidColorFromTexture;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MooFluids.MOD_ID, value = Dist.CLIENT)
public class ClientModEventHandler {

    public static boolean donePopulation = false;

    @SubscribeEvent
    public static void onTexturePostStitch(TextureStitchEvent.Post event) {
        if (donePopulation) return;
        FluidColorFromTexture.populate();
        donePopulation = true;
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
