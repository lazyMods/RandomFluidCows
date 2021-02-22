package lazy.moofluids.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;

import java.util.List;
import java.util.Objects;

public class MooFluidReg {

    private static final List<Fluid> FLUIDS = Lists.newArrayList();

    public static boolean add(Fluid fluid) {
        if (exists(fluid)) return false;
        if (fluid == Fluids.EMPTY) return false;
        if (!fluid.isSource(fluid.getDefaultState())) return false;
        FLUIDS.add(fluid);
        return true;
    }

    public static boolean exists(Fluid fluid) {
        return FLUIDS.stream().anyMatch(fluidIn -> fluid == fluidIn);
    }

    public static Fluid get(String registryName) {
        return FLUIDS.stream().filter(fluid -> Objects.requireNonNull(fluid.getRegistryName()).toString().equals(registryName)).findFirst().orElse(null);
    }

    public static ImmutableList<Fluid> getFluids() {
        return ImmutableList.copyOf(FLUIDS);
    }
}
