package lazy.moofluids.impl;

import lazy.moofluids.block.AutoMilkerBlock;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.tile.AutoMilkerTile;
import mcp.mobius.waila.api.*;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

@WailaPlugin
public class HwylaPlugin implements IWailaPlugin {

    @Override
    public void register(IRegistrar iRegistrar) {
        iRegistrar.registerComponentProvider(new EntityComponentProvider(), TooltipPosition.BODY, MooFluidEntity.class);
        iRegistrar.registerComponentProvider(new BlockComponentProvider(), TooltipPosition.BODY, AutoMilkerBlock.class);
    }

    public static class EntityComponentProvider implements IEntityComponentProvider {
        @Override
        public void appendBody(List<ITextComponent> tooltip, IEntityAccessor accessor, IPluginConfig config) {
            if (accessor.getEntity() instanceof MooFluidEntity) {
                MooFluidEntity mooFluidEntity = (MooFluidEntity) accessor.getEntity();
                String fluidName = mooFluidEntity.getFluid() == Fluids.EMPTY || mooFluidEntity.getFluid() == null ? "Empty" : new FluidStack(mooFluidEntity.getFluid(), 1000).getDisplayName().getString();
                tooltip.add(new StringTextComponent("Fluid: " + fluidName));
                String display = mooFluidEntity.canBeMilked() ? "Can be milked!" : "Delay: " + mooFluidEntity.getDelay();
                tooltip.add(new StringTextComponent(display));
            }
        }
    }

    public static class BlockComponentProvider implements IComponentProvider {
        @Override
        public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
            if (accessor.getTileEntity() instanceof AutoMilkerTile) {
                AutoMilkerTile autoMilkerTile = (AutoMilkerTile) accessor.getTileEntity();
                tooltip.add(new StringTextComponent("Capacity: " + autoMilkerTile.getFluidAmount() + "/" + autoMilkerTile.getCapacity()));
                tooltip.add(new StringTextComponent("Timer: " + autoMilkerTile.getTimer()));
            }
        }
    }
}
