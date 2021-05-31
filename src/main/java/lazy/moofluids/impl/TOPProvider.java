package lazy.moofluids.impl;

import lazy.moofluids.MooFluids;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.tile.AutoMilkerTile;
import mcjty.theoneprobe.api.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
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
        public void addProbeEntityInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, Entity entity, IProbeHitEntityData iProbeHitEntityData) {
            if (entity instanceof MooFluidEntity) {
                MooFluidEntity mooFluidEntity = (MooFluidEntity) entity;
                String fluidName = mooFluidEntity.getFluid() == Fluids.EMPTY || mooFluidEntity.getFluid() == null ? "Empty" : new FluidStack(mooFluidEntity.getFluid(), 1000).getDisplayName().getString();
                iProbeInfo.text(new StringTextComponent("Fluid: " + fluidName));
                String display = mooFluidEntity.canBeMilked() ? "Can be milked!" : "Delay: " + mooFluidEntity.getDelay();
                iProbeInfo.text(new StringTextComponent(display));
            }
        }
    }

    public static class BlockProbeInfoProvider implements IProbeInfoProvider {

        @Override
        public String getID() {
            return MooFluids.MOD_ID + ":top_block_provider";
        }

        @Override
        public void addProbeInfo(ProbeMode probeMode, IProbeInfo iProbeInfo, PlayerEntity playerEntity, World world, BlockState blockState, IProbeHitData iProbeHitData) {
            if(world.getBlockEntity(iProbeHitData.getPos()) instanceof AutoMilkerTile){
                AutoMilkerTile autoMilkerTile = (AutoMilkerTile) world.getBlockEntity(iProbeHitData.getPos());
                if(autoMilkerTile != null){
                    iProbeInfo.text(new StringTextComponent("Capacity: " + autoMilkerTile.getFluidAmount() + "/" + autoMilkerTile.getCapacity()));
                    iProbeInfo.text(new StringTextComponent("Timer: " + autoMilkerTile.getTimer()));
                }
            }
        }
    }
}