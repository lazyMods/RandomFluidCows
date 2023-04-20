package lazy.moofluids.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
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
            if (((IClientFluidTypeExtensions) fluid.getFluidType().getRenderPropertiesInternal()).getTintColor() == -1) {
                ResourceLocation res = ((IClientFluidTypeExtensions) fluid.getFluidType().getRenderPropertiesInternal()).getStillTexture();
                ResourceLocation full = new ResourceLocation(res.getNamespace(), "textures/" + res.getPath() + ".png");
                try {
                    var inputStream = Minecraft.getInstance().getResourceManager().getResource(full);
                    if (inputStream.isPresent()) {
                        BufferedImage image = ImageIO.read(inputStream.get().open());
                        COLORS.put(fluid, ImageDominantColor.getColor(image, false));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
