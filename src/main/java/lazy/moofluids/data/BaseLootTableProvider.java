package lazy.moofluids.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.data.LootTableProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTableManager;
import net.minecraft.util.ResourceLocation;
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
    protected final Map<EntityType, LootTable.Builder> entityLootTables = new HashMap<>();
    private final DataGenerator generator;

    public BaseLootTableProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
        this.generator = dataGeneratorIn;
    }

    protected abstract void addTables();

    @Override
    public void act(@Nonnull DirectoryCache cache) {
        this.addTables();

        Map<ResourceLocation, LootTable> tables = new HashMap<>();
        blockLootTables.forEach((k, v)-> tables.put(k.getLootTable(), v.setParameterSet(LootParameterSets.BLOCK).build()));
        entityLootTables.forEach((k, v)-> tables.put(k.getLootTable(), v.setParameterSet(LootParameterSets.ENTITY).build()));

        this.writeTables(cache, tables);
    }

    private void writeTables(DirectoryCache cache, Map<ResourceLocation, LootTable> tables) {
        Path outputFolder = this.generator.getOutputFolder();
        tables.forEach((key, lootTable) -> {
            Path path = outputFolder.resolve("data/" + key.getNamespace() + "/loot_tables/" + key.getPath() + ".json");
            try {
                IDataProvider.save(GSON, cache, LootTableManager.toJson(lootTable), path);
            } catch (IOException e) {
                LOGGER.error("Couldn't write loot table {}", path, e);
            }
        });
    }
}