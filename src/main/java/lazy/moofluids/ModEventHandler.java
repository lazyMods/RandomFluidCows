package lazy.moofluids;

import lazy.moofluids.utils.FluidColorFromTexture;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MooFluids.MOD_ID, value = Dist.CLIENT)
public class ModEventHandler {

    public static boolean donePopulation = false;

    @SubscribeEvent
    public static void onTexturePostStitch(TextureStitchEvent.Post event) {
        if (donePopulation) return;
        FluidColorFromTexture.populate();
        donePopulation = true;
    }
}
