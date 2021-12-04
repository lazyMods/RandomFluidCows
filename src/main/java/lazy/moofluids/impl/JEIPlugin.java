package lazy.moofluids.impl;

//@JeiPlugin
public class JEIPlugin /*implements IModPlugin*/ {

    /*@Override
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
        public void draw(Fluid fluid, @Nonnull MatrixStack matrixStack, double mouseX, double mouseY) {
            FontRenderer fr = Minecraft.getInstance().font;
            FluidStack stack = new FluidStack(fluid.getFluid(), 1);
            String stackName = stack.getDisplayName().getString();
            if(stackName.contains(".")) stackName = stackName.split("\\.")[2];
            StringTextComponent toDisplay = new StringTextComponent("MooFluid with " + stackName);
            int y = 22;
            for (IReorderingProcessor rp : fr.split(toDisplay, 87)) {
                fr.drawInternal(rp, 0, y, 0, matrixStack.last().pose(), false);
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
            iIngredients.setOutput(VanillaTypes.ITEM, new ItemStack(fluid.getBucket()));
        }

        @Override
        @ParametersAreNonnullByDefault
        public void setRecipe(IRecipeLayout iRecipeLayout, Fluid fluid, IIngredients iIngredients) {
            IGuiItemStackGroup guiItemStacks = iRecipeLayout.getItemStacks();
            guiItemStacks.init(0, true, 0, 0);
            guiItemStacks.init(1, false, 69, 0);
            guiItemStacks.set(iIngredients);
        }
    }*/
}
