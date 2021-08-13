package lazy.moofluids;

import lazy.moofluids.entity.MooFluidEntity;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MooFluids.MOD_ID)
public class ModEventHandler {

    @SubscribeEvent
    public static void onEntityAttributeRegister(EntityAttributeCreationEvent event) {
        event.put(Setup.MOO_FLUID.get(), MooFluidEntity.createAttr());
    }
}
