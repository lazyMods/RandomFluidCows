package lazy.moofluids;

import lazy.moofluids.entity.MooFluidEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.material.Fluids;
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
        if (event.getCategory() == Biome.BiomeCategory.NETHER
                || event.getCategory() == Biome.BiomeCategory.OCEAN
                || event.getCategory() == Biome.BiomeCategory.BEACH
                || event.getCategory() == Biome.BiomeCategory.DESERT
                || event.getCategory() == Biome.BiomeCategory.RIVER
                || event.getCategory() == Biome.BiomeCategory.THEEND) return;
        List<MobSpawnSettings.SpawnerData> spawners = event.getSpawns().getSpawner(MobCategory.CREATURE);
        spawners.add(new MobSpawnSettings.SpawnerData(Setup.MOO_FLUID.get(), Configs.SPAWN_WEIGHT.get(), Configs.SPAWN_MIN_COUNT.get(), Configs.SPAWN_MAX_COUNT.get()));
    }

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
        if (event.getWorld().isClientSide) return;
        if (event.getEntity() instanceof MooFluidEntity mooFluidEntity) {
            if (mooFluidEntity.getFluid() == null || mooFluidEntity.getFluid() == Fluids.EMPTY) {
                mooFluidEntity.setHealth(0);
            }
        }
    }
}
