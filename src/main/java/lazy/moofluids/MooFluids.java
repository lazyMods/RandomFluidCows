package lazy.moofluids;

import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.impl.TOPProvider;
import lazy.moofluids.utils.MooFluidReg;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(MooFluids.MOD_ID)
public class MooFluids {

    public static final String MOD_ID = "moofluids";
    public static final String TOP_ID = "theoneprobe";

    public MooFluids() {
        Setup.init();
        Configs.registerAndLoadConfig();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::interModEnqueue);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onLoadComplete);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    public void onLoadComplete(FMLLoadCompleteEvent event) {

        //TODO:: Add custom buckets
        //Add fluids that don't have a bucket form.
//        if (Configs.SPAWN_COWS_WITH_BUCKETLESS_FLUIDS.get()) {
//            ForgeRegistries.FLUIDS.forEach(MooFluidReg::add);
//        } else {
        for (Fluid fluid : ForgeRegistries.FLUIDS) {
            if (fluid.getBucket() != ItemStack.EMPTY.getItem()) {
                MooFluidReg.add(fluid);
            }
        }
//        }
//        //Generate custom buckets to hold the fluids that don't have a bucket form
//        if (Configs.GENERATED_CUSTOM_BUCKETS.get()) {
//            List<Fluid> fluidsWithoutBucket = Lists.newArrayList();
//            for (Fluid fluid : ForgeRegistries.FLUIDS) {
//                if (fluid.getBucket() == ItemStack.EMPTY.getItem()) {
//                    fluidsWithoutBucket.add(fluid);
//                }
//            }
//            UniversalBucketItem universalBucket = (UniversalBucketItem) ForgeRegistries.ITEMS.getValue(new ResourceLocation(MOD_ID, "universal_bucket"));
//            if (universalBucket != null) {
//                universalBucket.setFluids(fluidsWithoutBucket);
//            }
//        }
    }

    public void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            SpawnPlacements.register(Setup.MOO_FLUID.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, MooFluidEntity::canSpawn);
        });
    }

    public void interModEnqueue(InterModEnqueueEvent event) {
        if (ModList.get().isLoaded(TOP_ID))
            TOPProvider.register();
    }
}
