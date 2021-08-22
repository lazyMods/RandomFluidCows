package lazy.moofluids;

import com.google.common.collect.Lists;
import lazy.moofluids.client.render.MooFluidRenderer;
import lazy.moofluids.client.screen.AutoMilkerScreen;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.impl.TOPProvider;
import lazy.moofluids.item.UniversalBucketItem;
import lazy.moofluids.utils.MooFluidReg;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

@Mod(MooFluids.MOD_ID)
public class MooFluids {

    public static final String MOD_ID = "moofluids";

    public MooFluids() {
        Setup.init();
        Configs.registerAndLoadConfig();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::interModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);
    }

    public void onClientSetup(FMLClientSetupEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(Setup.MOO_FLUID.get(), MooFluidRenderer::new);
        ScreenManager.register(Setup.AUTO_MILKER_CONTAINER.get(), AutoMilkerScreen::new);
    }

    public void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            GlobalEntityTypeAttributes.put(Setup.MOO_FLUID.get(), MooFluidEntity.createAttr());
        });
    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {
        //Add fluids that don't have a bucket form.
        if (Configs.SPAWN_COWS_WITH_BUCKETLESS_FLUIDS.get()) {
            ForgeRegistries.FLUIDS.forEach(MooFluidReg::add);
        } else {
            for (Fluid fluid : ForgeRegistries.FLUIDS) {
                if (fluid.getBucket() != ItemStack.EMPTY.getItem()) {
                    MooFluidReg.add(fluid);
                }
            }
        }
        //Generate custom buckets to hold the fluids that don't have a bucket form
        if (Configs.GENERATED_CUSTOM_BUCKETS.get()) {
            List<Fluid> fluidsWithoutBucket = Lists.newArrayList();
            for (Fluid fluid : ForgeRegistries.FLUIDS) {
                if (fluid.getBucket() == ItemStack.EMPTY.getItem()) {
                    fluidsWithoutBucket.add(fluid);
                }
            }
            UniversalBucketItem universalBucket = (UniversalBucketItem) ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID, "universal_bucket"));
            if (universalBucket != null) {
                universalBucket.setFluids(fluidsWithoutBucket);
            }
        }
    }

    @SubscribeEvent
    public void interModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded("theoneprobe"))
            TOPProvider.register();
    }
}
