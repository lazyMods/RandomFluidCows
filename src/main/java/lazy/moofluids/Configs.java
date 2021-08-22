package lazy.moofluids;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

public class Configs {

    public static ForgeConfigSpec COMMON;

    public static ForgeConfigSpec.IntValue SPAWN_WEIGHT;
    public static ForgeConfigSpec.IntValue SPAWN_MIN_COUNT;
    public static ForgeConfigSpec.IntValue SPAWN_MAX_COUNT;
    public static ForgeConfigSpec.BooleanValue GENERATED_CUSTOM_BUCKETS;
    public static ForgeConfigSpec.BooleanValue SPAWN_COWS_WITH_BUCKETLESS_FLUIDS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push(MooFluids.MOD_ID);
        SPAWN_WEIGHT = builder.comment("Spawn Weight - Change of spawning.").defineInRange("spawn_weight", 2, 1, Integer.MAX_VALUE);
        SPAWN_MIN_COUNT = builder.comment("Spawn Min Count - The lowest number of cows that should spawn.").defineInRange("spawn_min_count", 1, 1, Integer.MAX_VALUE);
        SPAWN_MAX_COUNT = builder.comment("Spawn Max Count - The max number of cows that should spawn.").defineInRange("spawn_max_count", 1, 1, Integer.MAX_VALUE);
        GENERATED_CUSTOM_BUCKETS = builder.comment("Generates bucket for fluids that don't have one. This may cause issues, if so disable this option.").define("custom_buckets", true);
        SPAWN_COWS_WITH_BUCKETLESS_FLUIDS = builder.comment("If enabled the game will spawn cows with fluids that don't have a bucket version.",
                "If this is enabled, you need to have custom_buckets enabled to get the fluids in bucket form.").define("spawn_cows_with_bucketless_fluids", true);
        builder.pop();

        COMMON = builder.build();
    }

    public static void registerAndLoadConfig() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, COMMON);
        CommentedFileConfig config = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(MooFluids.MOD_ID.concat("-common.toml"))).sync().writingMode(WritingMode.REPLACE).build();
        config.load();
        COMMON.setConfig(config);
    }
}