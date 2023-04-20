package lazy.moofluids.utils;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Objects;

public class MooFluidReg {

    private static final List<Fluid> FLUIDS = Lists.newArrayList();

    public static void add(Fluid fluid) {
        if (exists(fluid)) return;
        if (fluid == Fluids.EMPTY) return;
        if (!fluid.isSource(fluid.defaultFluidState())) return;
        FLUIDS.add(fluid);
    }

    public static boolean exists(Fluid fluid) {
        return FLUIDS.stream().anyMatch(fluidIn -> fluid == fluidIn);
    }

    public static Fluid get(String registryName) {
        return FLUIDS.stream().filter(fluid -> ForgeRegistries.FLUIDS.getKey(fluid).toString().equals(registryName)).findFirst().orElse(null);
    }

    public static ImmutableList<Fluid> getFluids() {
        return ImmutableList.copyOf(FLUIDS);
    }
}
