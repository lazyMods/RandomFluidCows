package lazy.moofluids.data;

import lazy.moofluids.MooFluids;
import lazy.moofluids.Setup;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.LootingEnchantFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

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
        }
        if (event.includeServer()) {
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
                    LootTable.lootTable().withPool(LootPool.lootPool().name("auto_milker").add(LootItem.lootTableItem(Setup.AUTO_MILKER.get())))
            );
            this.entityLootTables.put(Setup.MOO_FLUID.get(),
                    LootTable.lootTable()
                            .withPool(LootPool.lootPool()
                                    .setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(Items.LEATHER)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                            .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
                                    )
                            )
                            .withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                                    .add(LootItem.lootTableItem(Items.BEEF)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                            .apply(SmeltItemFunction.smelted().when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().flags(EntityFlagsPredicate.Builder.flags().setOnFire(true).build()))))
                                            .apply(LootingEnchantFunction.lootingMultiplier(UniformGenerator.between(0.0F, 1.0F)))
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
        protected void buildCraftingRecipes(Consumer<FinishedRecipe> recipeConsumer) {
            ShapedRecipeBuilder.shaped(Setup.AUTO_MILKER.get())
                    .pattern("xxx")
                    .pattern("x#x")
                    .pattern("xxx")
                    .define('x', Tags.Items.INGOTS_IRON)
                    .define('#', Items.BUCKET)
                    .unlockedBy("iron_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_INGOT))
                    .save(recipeConsumer);
        }
    }
}
