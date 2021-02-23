package lazy.moofluids;

import lazy.moofluids.client.render.MooFluidRenderer;
import lazy.moofluids.client.screen.AutoMilkerScreen;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.impl.TOPProvider;
import lazy.moofluids.utils.FluidColorFromTexture;
import lazy.moofluids.utils.MooFluidReg;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(MooFluids.MOD_ID)
public class MooFluids {

    public static final String MOD_ID = "moofluids";

    public MooFluids() {
        Setup.init();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::interModEnqueue);
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(Setup.MOO_FLUID.get(), MooFluidRenderer::new);
        ScreenManager.registerFactory(Setup.AUTO_MILKER_CONTAINER.get(), AutoMilkerScreen::new);
    }

    public void onSetup(FMLCommonSetupEvent event) {
        ForgeRegistries.FLUIDS.forEach(MooFluidReg::add);
        event.enqueueWork(() -> {
            GlobalEntityTypeAttributes.put(Setup.MOO_FLUID.get(), MooFluidEntity.createAttr());
        });
    }

    @SubscribeEvent
    public void interModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe"))
            TOPProvider.register();
    }
}
