package lazy.moofluids;

import lazy.moofluids.entity.MooFluidEntity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.fluid.Fluids;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        if(event.getCategory() == Biome.Category.NETHER
                || event.getCategory() == Biome.Category.OCEAN
                || event.getCategory() == Biome.Category.BEACH
                || event.getCategory() == Biome.Category.DESERT
                || event.getCategory() == Biome.Category.RIVER
                || event.getCategory() == Biome.Category.THEEND) return;
        List<MobSpawnInfo.Spawners> spawners = event.getSpawns().getSpawner(EntityClassification.CREATURE);
        spawners.add(new MobSpawnInfo.Spawners(Setup.MOO_FLUID.get(), Configs.SPAWN_WEIGHT.get(), Configs.SPAWN_MIN_COUNT.get(), Configs.SPAWN_MAX_COUNT.get()));
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if(event.getWorld().isClientSide) return;
        if(event.getEntity() instanceof MooFluidEntity) {
            MooFluidEntity mooFluidEntity = (MooFluidEntity) event.getEntity();
            if(mooFluidEntity.getFluid() == null || mooFluidEntity.getFluid() == Fluids.EMPTY) {
                mooFluidEntity.setHealth(0);
            }
        }
    }
}
