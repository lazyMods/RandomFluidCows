package lazy.moofluids.impl;

import lazy.moofluids.entity.MooFluidEntity;
import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;

@WailaPlugin
public class JadePlugin implements IWailaPlugin {

    @Override
    public void register(IRegistrar iRegistrar) {
        iRegistrar.registerComponentProvider(new EntityComponentProvider(), TooltipPosition.BODY, MooFluidEntity.class);
    }

    public static class EntityComponentProvider implements IEntityComponentProvider {

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
            if (accessor.getEntity() instanceof MooFluidEntity mooFluidEntity) {
                String fluidName = mooFluidEntity.getFluid() == Fluids.EMPTY || mooFluidEntity.getFluid() == null ? "Empty" : new FluidStack(mooFluidEntity.getFluid(), 1000).getDisplayName().getString();
                tooltip.add(new TextComponent("Fluid: " + fluidName));
                String display = mooFluidEntity.canBeMilked() ? "Can be milked!" : "Delay: " + mooFluidEntity.getDelay();
                tooltip.add(new TextComponent(display));
            }
        }
    }
}
