package lazy.moofluids;

import com.google.common.collect.Lists;
import lazy.moofluids.item.UniversalBucketItem;
import lazy.moofluids.utils.MooFluidReg;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
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
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::interModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);
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
        //if (ModList.get().isLoaded("theoneprobe"))
        //TOPProvider.register();
    }
}
