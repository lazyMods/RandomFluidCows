package lazy.moofluids;

import lazy.moofluids.entity.MooFluidEntity;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, modid = MooFluids.MOD_ID)
public class Setup {

    public static final TagKey<Biome> ALLOWED = TagKey.create(Registry.BIOME_REGISTRY, new ResourceLocation("moofluids:allowed"));

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, MooFluids.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MooFluids.MOD_ID);
    public static final RegistryObject<EntityType<MooFluidEntity>> MOO_FLUID = ENTITIES.register("moo_fluid", () ->
            EntityType.Builder.of(MooFluidEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 1.4F).setTrackingRange(10)
                    .build("moo_fluid"));

    //TODO: Add custom buckets
    //public static final RegistryObject<Item> UNIVERSAL_BUCKET = ITEMS.register("universal_bucket", () -> new UniversalBucketItem(Lists.newArrayList()));

    public static void init() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent e) {
        e.getDispatcher().register(ModCommands.findCowsNearby());
        e.getDispatcher().register(ModCommands.spawnCow(e.getBuildContext()));
    }
}
