package lazy.moofluids;

import lazy.moofluids.entity.MooFluidEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class EventHandler {

    @SubscribeEvent
    public static void onEntityJoinWorld(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide) return;
        if (event.getEntity() instanceof MooFluidEntity mooFluidEntity) {
            if (mooFluidEntity.getFluid() == null || mooFluidEntity.getFluid() == Fluids.EMPTY) {
                mooFluidEntity.setHealth(0);
            }
        }
    }
}
