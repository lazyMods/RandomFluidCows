package lazy.moofluids.impl;

import lazy.moofluids.MooFluids;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.tile.AutoMilkerTile;
import mcjty.theoneprobe.api.*;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
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
            iTheOneProbe.registerProvider(new BlockProbeInfoProvider());
            return null;
        }
    }

    public static class EntityProbeInfoProvider implements IProbeInfoEntityProvider {

        @Override
        public String getID() {
            return MooFluids.MOD_ID + ":top_entity_provider";
        }

        @Override
        public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, net.minecraft.world.level.Level level, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
            if (entity instanceof MooFluidEntity mooFluidEntity) {
                String fluidName = mooFluidEntity.getFluid() == Fluids.EMPTY || mooFluidEntity.getFluid() == null ? "Empty" : new FluidStack(mooFluidEntity.getFluid(), 1000).getDisplayName().getString();
                iProbeInfo.text(new TextComponent("Fluid: " + fluidName));
                String display = mooFluidEntity.canBeMilked() ? "Can be milked!" : "Delay: " + mooFluidEntity.getDelay();
                iProbeInfo.text(new TextComponent(display));
            }
        }
    }

    public static class BlockProbeInfoProvider implements IProbeInfoProvider {

        @Override
        public ResourceLocation getID() {
            return new ResourceLocation(MooFluids.MOD_ID, "top_block_provider");
        }

        @Override
        public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, Player player, Level level, BlockState blockState, IProbeHitData iProbeHitData) {
            if (level.getBlockEntity(iProbeHitData.getPos()) instanceof AutoMilkerTile autoMilkerTile) {
                iProbeInfo.text(new TextComponent("Capacity: " + autoMilkerTile.getFluidAmount() + "/" + autoMilkerTile.getCapacity()));
                iProbeInfo.text(new TextComponent("Timer: " + autoMilkerTile.getTimer()));
            }
        }
    }
}