package lazy.moofluids.impl;

import lazy.moofluids.block.AutoMilkerBlock;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.tile.AutoMilkerTile;
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
        iRegistrar.registerComponentProvider(new BlockComponentProvider(), TooltipPosition.BODY, AutoMilkerBlock.class);
    }

    public static class EntityComponentProvider implements IEntityComponentProvider {

        @Override
        public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
            if (entityAccessor.getEntity() instanceof MooFluidEntity mooFluidEntity) {
                String fluidName = mooFluidEntity.getFluid() == Fluids.EMPTY || mooFluidEntity.getFluid() == null ? "Empty" : new FluidStack(mooFluidEntity.getFluid(), 1000).getDisplayName().getString();
                iTooltip.add(new TextComponent("Fluid: " + fluidName));
                String display = mooFluidEntity.canBeMilked() ? "Can be milked!" : "Delay: " + mooFluidEntity.getDelay();
                iTooltip.add(new TextComponent(display));
            }
        }
    }

    public static class BlockComponentProvider implements IComponentProvider {
        @Override
        public void appendTooltip(ITooltip iTooltip, BlockAccessor entityAccessor, IPluginConfig iPluginConfig) {
            if (entityAccessor.getBlockEntity() instanceof AutoMilkerTile autoMilkerTile) {
                iTooltip.add(new TextComponent("Capacity: " + autoMilkerTile.getFluidAmount() + "/" + autoMilkerTile.getCapacity()));
                iTooltip.add(new TextComponent("Timer: " + autoMilkerTile.getTimer()));
            }
        }
    }
}
