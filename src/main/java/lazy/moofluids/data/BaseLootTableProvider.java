package lazy.moofluids.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mcjty
 * https://github.com/McJty/YouTubeModding14/blob/06097eee7db535d55c6cbe0bb4a523b07335fa33/src/main/java/com/mcjty/mytutorial/datagen/BaseLootTableProvider.java#L23
 * @author lazynessmind
 * - Added entity loot tables
 * - Remove the standart table method.
 */
public abstract class BaseLootTableProvider extends LootTableProvider {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    protected final Map<Block, LootTable.Builder> blockLootTables = new HashMap<>();
    @SuppressWarnings("rawtypes")
    protected final Map<EntityType, LootTable.Builder> entityLootTables = new HashMap<>();
    private final DataGenerator generator;

    public BaseLootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
        this.generator = dataGeneratorIn;
    }

    protected abstract void addTables();

    @Override
    public void run(@Nonnull HashCache cache) {
        this.addTables();

        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        blockLootTables.forEach((k, v) -> tables.put(k.getLootTable(), v.setParamSet(LootContextParamSets.BLOCK).build()));
        entityLootTables.forEach((k, v) -> tables.put(k.getDefaultLootTable(), v.setParamSet(LootContextParamSets.ENTITY).build()));

        this.writeTables(cache, tables);
    }

    private void writeTables(HashCache cache, Map<ResourceLocation, LootTable> tables) {
        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                DataProvider.save(GSON, cache, LootTables.serialize(lootTable), path);
            } catch (IOException e) {
                LOGGER.error("Couldn't write loot table {}", path, e);
            }
        });
    }
}