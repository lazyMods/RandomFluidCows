package lazy.moofluids.impl;

import com.mojang.blaze3d.vertex.PoseStack;
import lazy.moofluids.MooFluids;
import lazy.moofluids.utils.MooFluidReg;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@JeiPlugin
public class JEIPlugin implements IModPlugin {

    @Override
    @Nonnull
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(MooFluids.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MooFluidCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RecipeType.create(MooFluids.MOD_ID, "fluid", Fluid.class), MooFluidReg.getFluids());
    }

    public static class MooFluidCategory implements IRecipeCategory<Fluid> {

        private final IDrawable overlay;
        private final IDrawable icon;

        public static final ResourceLocation ID = new ResourceLocation(MooFluids.MOD_ID, "moofluid_category");
        public static final ResourceLocation OVERLAY = new ResourceLocation(MooFluids.MOD_ID, "textures/jei_overlay.png");

        public MooFluidCategory(IGuiHelper guiHelper) {
            overlay = guiHelper.createDrawable(OVERLAY, 0, 0, 87, 38);
            icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.BUCKET));
        }

        @Override
        public void draw(Fluid fluid, IRecipeSlotsView slotsView, @Nonnull PoseStack matrixStack, double mouseX, double mouseY) {
            Font fr = Minecraft.getInstance().font;
            FluidStack stack = new FluidStack(fluid, 1);
            String stackName = stack.getDisplayName().getString();
            if (stackName.contains(".")) stackName = stackName.split("\\.")[2];
            var toDisplay = Component.literal("MooFluid with " + stackName);
            int y = 22;
            for (FormattedCharSequence rp : fr.split(toDisplay, 87)) {
                fr.drawInternal(rp, 0, y, 0, matrixStack.last().pose(), false);
                y += 9;
            }
        }

        @Override
        @Nonnull
        public Component getTitle() {
            return Component.literal("Moo Fluids");
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
        @ParametersAreNonnullByDefault
        public void setRecipe(IRecipeLayoutBuilder iRecipeLayout, Fluid fluid, IFocusGroup iIngredients) {
            iRecipeLayout.addSlot(RecipeIngredientRole.INPUT, 1, 1).addItemStack(new ItemStack(Items.BUCKET));
            iRecipeLayout.addSlot(RecipeIngredientRole.OUTPUT, 70, 1).addItemStack(new ItemStack(fluid.getBucket()));
        }

        @Override
        public RecipeType<Fluid> getRecipeType() {
            return RecipeType.create(MooFluids.MOD_ID, "fluid", Fluid.class);
        }
    }
}
