package lazy.moofluids.impl;

import lazy.moofluids.MooFluids;
import lazy.moofluids.entity.MooFluidEntity;
import mcjty.theoneprobe.api.*;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.InterModComms;

import java.util.function.Function;

public class TOPProvider {

    private static boolean done;

    public static void register() {
        if (done) return;
        done = true;
        InterModComms.sendTo("theoneprobe", "getTheOneProbe", GetTheOneProbe::new);
    }

    public static class GetTheOneProbe implements Function<ITheOneProbe, Void> {

        @Override
        public Void apply(ITheOneProbe iTheOneProbe) {
            iTheOneProbe.registerEntityProvider(new EntityProbeInfoProvider());
            return null;
        }
    }

    public static class EntityProbeInfoProvider implements IProbeInfoEntityProvider {

        @Override
        public String getID() {
            return MooFluids.MOD_ID + ":top_entity_provider";
        }

        @Override
        public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player playerEntity, Level world, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
            if (entity instanceof MooFluidEntity mooFluidEntity) {
                String fluidName = mooFluidEntity.getFluid() == Fluids.EMPTY || mooFluidEntity.getFluid() == null ? "Empty" : new FluidStack(mooFluidEntity.getFluid(), 1000).getDisplayName().getString();
                iProbeInfo.text(Component.literal("Fluid: " + fluidName));
                String display = mooFluidEntity.canBeMilked() ? "Can be milked!" : "Delay: " + mooFluidEntity.getDelay();
                iProbeInfo.text(Component.literal(display));
            }
        }
    }
}