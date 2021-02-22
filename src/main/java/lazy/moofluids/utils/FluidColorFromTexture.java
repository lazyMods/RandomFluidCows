package lazy.moofluids.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class FluidColorFromTexture {

    public static final Map<Fluid, Integer> COLORS = new HashMap<>();

    public static void populate() {
        for (Fluid fluid : MooFluidReg.getFluids()) {
            if (fluid.getAttributes().getColor() == -1) {
                ResourceLocation res = fluid.getAttributes().getStillTexture();
                ResourceLocation full = new ResourceLocation(res.getNamespace(), "textures/" + res.getPath() + ".png");
                try {
                    InputStream inputStream = Minecraft.getInstance().getResourceManager().getResource(full).getInputStream();
                    BufferedImage image = ImageIO.read(inputStream);
                    COLORS.put(fluid, ImageDominantColor.getColor(image));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Fluid getFluidFromColor(int color) {
        if (COLORS.containsValue(color))
            return COLORS.keySet().stream().filter(k -> COLORS.get(k) == color).findFirst().orElse(Fluids.EMPTY);
        return ForgeRegistries.FLUIDS.getValues().stream().filter(f -> f.getAttributes().getColor() == color).findFirst().orElse(Fluids.EMPTY);
    }
}
