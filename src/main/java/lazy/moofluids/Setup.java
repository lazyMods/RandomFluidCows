package lazy.moofluids;

import com.google.common.collect.Lists;
import lazy.moofluids.block.AutoMilkerBlock;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.inventory.container.AutoMilkerContainer;
import lazy.moofluids.item.UniversalBucketItem;
import lazy.moofluids.tile.AutoMilkerTile;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class Setup {

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, MooFluids.MOD_ID);
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MooFluids.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MooFluids.MOD_ID);
    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MooFluids.MOD_ID);
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, MooFluids.MOD_ID);

    public static final RegistryObject<EntityType<MooFluidEntity>> MOO_FLUID = ENTITIES.register("moo_fluid", () ->
            EntityType.Builder.of(MooFluidEntity::new, EntityClassification.CREATURE)
                    .sized(0.9F, 1.4F).setTrackingRange(10)
                    .build("moo_fluid"));

    public static final RegistryObject<Block> AUTO_MILKER = BLOCKS.register("auto_milker", AutoMilkerBlock::new);

    public static final RegistryObject<Item> AUTO_MILKER_ITEM = ITEMS.register("auto_milker",
            () -> new BlockItem(AUTO_MILKER.get(), new Item.Properties().tab(ItemGroup.TAB_MISC)));

    public static final RegistryObject<Item> UNIVERSAL_BUCKET = ITEMS.register("universal_bucket", () -> new UniversalBucketItem(Lists.newArrayList()));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<TileEntityType<AutoMilkerTile>> AUTO_MILKER_TYPE = TILES.register("auto_milker",
            () -> TileEntityType.Builder.of(AutoMilkerTile::new, AUTO_MILKER.get()).build(null));

    public static final RegistryObject<ContainerType<AutoMilkerContainer>> AUTO_MILKER_CONTAINER = CONTAINERS.register("auto_milker",
            () -> IForgeContainerType.create((windowId, inv, data) -> new AutoMilkerContainer(windowId, inv)));

    public static void init() {
        BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        TILES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ENTITIES.register(FMLJavaModLoadingContext.get().getModEventBus());
        CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
