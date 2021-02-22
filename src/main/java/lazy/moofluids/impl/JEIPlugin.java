package lazy.moofluids.impl;

import com.mojang.blaze3d.matrix.MatrixStack;
import lazy.moofluids.MooFluids;
import lazy.moofluids.utils.MooFluidReg;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MooFluids.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MooFluidCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(MooFluidReg.getFluids(), MooFluidCategory.ID);
    }

    public static class MooFluidCategory implements IRecipeCategory<Fluid> {

        private final IDrawable overlay;
        private final IDrawable icon;

        public static final ResourceLocation ID = new ResourceLocation(MooFluids.MOD_ID, "moofluid_category");
        public static final ResourceLocation OVERLAY = new ResourceLocation(MooFluids.MOD_ID, "textures/jei_overlay.png");

        public MooFluidCategory(IGuiHelper guiHelper) {
            overlay = guiHelper.createDrawable(OVERLAY, 0, 0, 87, 38);
            icon = guiHelper.createDrawableIngredient(new ItemStack(Items.BUCKET));
        }

        @Override
        public void draw(Fluid fluid, MatrixStack matrixStack, double mouseX, double mouseY) {
            FontRenderer fr = Minecraft.getInstance().fontRenderer;
            FluidStack stack = new FluidStack(fluid.getFluid(),1);
            String stackName = stack.getDisplayName().getString();
            if(stackName.contains(".")) stackName = stackName.split("\\.")[2];
            StringTextComponent toDisplay = new StringTextComponent("MooFluid with " + stackName);
            int y = 22;
            for(IReorderingProcessor rp : fr.trimStringToWidth(toDisplay, 87)) {
                fr.func_238415_a_(rp, 0, y, 0, matrixStack.getLast().getMatrix(), false);
                y += 9;
            }
        }

        @Override
        @Nonnull
        public ResourceLocation getUid() {
            return ID;
        }


        @Override
        @Nonnull
        public Class<? extends Fluid> getRecipeClass() {
            return Fluid.class;
        }

        @Override
        @Nonnull
        public String getTitle() {
            return "Moo Fluids";
        }

        @Override
        @Nonnull
        public IDrawable getBackground() {
            return this.overlay;
        }

        @Override
        @Nonnull
        public IDrawable getIcon() {
            return this.icon;
        }

        @Override
        public void setIngredients(Fluid fluid, IIngredients iIngredients) {
            iIngredients.setInput(VanillaTypes.ITEM, new ItemStack(Items.BUCKET));
            iIngredients.setOutput(VanillaTypes.ITEM, new ItemStack(fluid.getFilledBucket()));
        }

        @Override
        public void setRecipe(IRecipeLayout iRecipeLayout, Fluid fluid, IIngredients iIngredients) {
            IGuiItemStackGroup guiItemStacks = iRecipeLayout.getItemStacks();
            guiItemStacks.init(0, true, 0, 0);
            guiItemStacks.init(1, false, 69, 0);
            guiItemStacks.set(iIngredients);
        }
    }
}
