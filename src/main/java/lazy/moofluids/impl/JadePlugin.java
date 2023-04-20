package lazy.moofluids.impl;

import lazy.moofluids.MooFluids;
import lazy.moofluids.entity.MooFluidEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import snownee.jade.api.*;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    @Override
    public void registerClient(IWailaClientRegistration iRegistrar) {
        iRegistrar.registerEntityComponent(new EntityComponentProvider(), MooFluidEntity.class);
    }

    public static class EntityComponentProvider implements IEntityComponentProvider {


        @Override
        public ResourceLocation getUid() {
            return new ResourceLocation(MooFluids.MOD_ID, "jade_entity");
        }

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            if (accessor.getEntity() instanceof MooFluidEntity mooFluidEntity) {
                String fluidName = mooFluidEntity.getFluid() == Fluids.EMPTY || mooFluidEntity.getFluid() == null ? "Empty" : new FluidStack(mooFluidEntity.getFluid(), 1000).getDisplayName().getString();
                tooltip.add(Component.literal("Fluid: " + fluidName));
                String display = mooFluidEntity.canBeMilked() ? "Can be milked!" : "Delay: " + mooFluidEntity.getDelay();
                tooltip.add(Component.literal(display));
            }
        }
    }
}
