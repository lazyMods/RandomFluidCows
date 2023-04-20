package lazy.moofluids.data;

import lazy.moofluids.MooFluids;
import lazy.moofluids.Setup;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

public class ModTagProvider extends TagsProvider<Biome> {

    protected ModTagProvider(DataGenerator p_126546_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_126546_, BuiltinRegistries.BIOME, MooFluids.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags() {
        this.tag(Setup.ALLOWED).add(
                Biomes.GROVE,
                Biomes.OLD_GROWTH_PINE_TAIGA,
                Biomes.OLD_GROWTH_SPRUCE_TAIGA,
                Biomes.WINDSWEPT_HILLS,
                Biomes.WINDSWEPT_GRAVELLY_HILLS,
                Biomes.WINDSWEPT_FOREST,
                Biomes.SAVANNA,
                Biomes.SAVANNA_PLATEAU,
                Biomes.WINDSWEPT_SAVANNA,
                Biomes.FOREST,
                Biomes.FLOWER_FOREST,
                Biomes.BIRCH_FOREST,
                Biomes.OLD_GROWTH_BIRCH_FOREST,
                Biomes.TAIGA,
                Biomes.SNOWY_TAIGA,
                Biomes.DARK_FOREST,
                Biomes.SWAMP
        );
    }
}
