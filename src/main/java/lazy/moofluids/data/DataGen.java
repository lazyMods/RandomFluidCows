package lazy.moofluids.data;

import lazy.moofluids.MooFluids;
import lazy.moofluids.Setup;
import net.minecraft.advancements.criterion.EntityFlagsPredicate;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Items;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.EntityHasProperty;
import net.minecraft.loot.functions.LootingEnchantBonus;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.loot.functions.Smelt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.util.Objects;
import java.util.function.Consumer;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MooFluids.MOD_ID)
public class DataGen {

    @SubscribeEvent
    public static void onDataGen(GatherDataEvent event) {
        if (event.includeClient()) {
            event.getGenerator().addProvider(new BlockStateGen(event.getGenerator(), event.getExistingFileHelper()));
            event.getGenerator().addProvider(new LanguageGen(event.getGenerator(), "en_us"));
            event.getGenerator().addProvider(new ItemModelGen(event.getGenerator(), event.getExistingFileHelper()));
            event.getGenerator().addProvider(new LootTableGen(event.getGenerator()));
            event.getGenerator().addProvider(new RecipeGen(event.getGenerator()));
        }
    }

    public static class BlockStateGen extends BlockStateProvider {

        public BlockStateGen(DataGenerator gen, ExistingFileHelper exFileHelper) {
            super(gen, MooFluids.MOD_ID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            this.horizontalBlock(Setup.AUTO_MILKER.get(), this.models().orientable(
                    Objects.requireNonNull(Setup.AUTO_MILKER.get().getRegistryName()).toString(),
                    new ResourceLocation(MooFluids.MOD_ID, "blocks/auto_milker"),
                    new ResourceLocation(MooFluids.MOD_ID, "blocks/auto_milker_front"),
                    new ResourceLocation(MooFluids.MOD_ID, "blocks/auto_milker")));
        }
    }

    public static class LanguageGen extends LanguageProvider {

        public LanguageGen(DataGenerator gen, String locale) {
            super(gen, MooFluids.MOD_ID, locale);
        }

        @Override
        protected void addTranslations() {
            this.add(Setup.AUTO_MILKER_ITEM.get(), "Auto Milker");
        }
    }

    public static class ItemModelGen extends ItemModelProvider {

        public ItemModelGen(DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator, MooFluids.MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            this.getBuilder(Objects.requireNonNull(Setup.AUTO_MILKER.get().getRegistryName()).toString())
                    .parent(new ModelFile.UncheckedModelFile(new ResourceLocation(MooFluids.MOD_ID, "block/auto_milker")));

        }
    }

    public static class LootTableGen extends BaseLootTableProvider {

        public LootTableGen(DataGenerator dataGeneratorIn) {
            super(dataGeneratorIn);
        }

        @Override
        protected void addTables() {
            this.blockLootTables.put(Setup.AUTO_MILKER.get(),
                    LootTable.builder().addLootPool(new LootPool.Builder()
                            .name("auto_milker")
                            .addEntry(ItemLootEntry.builder(Setup.AUTO_MILKER.get()))
                    )
            );
            this.entityLootTables.put(Setup.MOO_FLUID.get(),
                    LootTable.builder().addLootPool(new LootPool.Builder()
                            .rolls(ConstantRange.of(1))
                            .addEntry(ItemLootEntry.builder(Items.LEATHER)
                                    .acceptFunction(SetCount.builder(RandomValueRange.of(0.0F, 2.0F)))
                                    .acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))))
                            .addLootPool(LootPool.builder().rolls(ConstantRange.of(1))
                                    .addEntry(ItemLootEntry.builder(Items.BEEF)
                                            .acceptFunction(SetCount.builder(RandomValueRange.of(1.0F, 3.0F)))
                                            .acceptFunction(Smelt.func_215953_b()
                                                    .acceptCondition(EntityHasProperty.builder(LootContext.EntityTarget.THIS,
                                                            EntityPredicate.Builder.create()
                                                                    .flags(EntityFlagsPredicate.Builder.create()
                                                                            .onFire(true).build()))))
                                            .acceptFunction(LootingEnchantBonus.builder(RandomValueRange.of(0.0F, 1.0F)))
                                    )
                            )
            );
        }
    }

    public static class RecipeGen extends RecipeProvider {
        public RecipeGen(DataGenerator generatorIn) {
            super(generatorIn);
        }

        @Override
        protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
            ShapedRecipeBuilder.shapedRecipe(Setup.AUTO_MILKER.get())
                    .patternLine("xxx")
                    .patternLine("x#x")
                    .patternLine("xxx")
                    .key('x', Tags.Items.INGOTS_IRON)
                    .key('#', Items.BUCKET)
                    .addCriterion("iron_ingot", InventoryChangeTrigger.Instance.forItems(Items.IRON_INGOT))
                    .build(consumer);
        }
    }
}
