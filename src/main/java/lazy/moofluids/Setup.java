package lazy.moofluids;

import com.google.common.collect.Lists;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.item.UniversalBucketItem;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class Setup {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MooFluids.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MooFluids.MOD_ID);

    public static final RegistryObject<EntityType<MooFluidEntity>> MOO_FLUID = ENTITIES.register("moo_fluid", () ->
            EntityType.Builder.of(MooFluidEntity::new, MobCategory.CREATURE)
                    .sized(0.9F, 1.4F).setTrackingRange(10)
                    .build("moo_fluid"));

    public static final RegistryObject<Item> UNIVERSAL_BUCKET = ITEMS.register("universal_bucket", () -> new UniversalBucketItem(Lists.newArrayList()));

    public static void init() {
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
