package lazy.moofluids.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Objects;

public class UniversalBucketItem extends Item {

    public static final String TAG_REGISTRY_NAME = "registryName";
    private List<Fluid> fluids;

    public UniversalBucketItem(List<Fluid> fluids) {
        super(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(16));
        this.fluids = fluids;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if (group != this.getItemCategory()) return;
        for (Fluid fluid : this.fluids) {
            ItemStack stack = new ItemStack(this);
            stack.setTag(new CompoundTag());
            if (stack.getTag() != null)
                stack.getTag().putString(TAG_REGISTRY_NAME, Objects.requireNonNull(fluid.getRegistryName()).toString());
            items.add(stack);
        }
    }

    @Override
    @Nonnull
    public Component getName(ItemStack stack) {
        TextComponent textComponent = new TextComponent("");
        if (stack.getTag() != null && stack.getTag().contains(TAG_REGISTRY_NAME)) {
            textComponent.append("Universal Bucket of ");
            String renameName = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stack.getOrCreateTag().getString(TAG_REGISTRY_NAME))).getName().getString();
            textComponent.append(renameName);
        }
        return textComponent.getString().equals("") ? super.getName(stack) : textComponent;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player playerEntity, InteractionHand hand) {
        ItemStack itemstack = playerEntity.getItemInHand(hand);
        String fluidRegName = itemstack.getOrCreateTag().getString(TAG_REGISTRY_NAME);
        Fluid content = fluidRegName.isEmpty() ? Fluids.EMPTY : this.get(fluidRegName);
        BlockHitResult blockhitresult = getPlayerPOVHitResult(world, playerEntity, content == Fluids.EMPTY ? ClipContext.Fluid.SOURCE_ONLY : ClipContext.Fluid.NONE);
        InteractionResultHolder<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerEntity, world, itemstack, blockhitresult);
        if (ret != null) return ret;
        if (blockhitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
        } else if (blockhitresult.getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemstack);
        } else {
            BlockPos blockpos = blockhitresult.getBlockPos();
            Direction direction = blockhitresult.getDirection();
            BlockPos blockpos1 = blockpos.relative(direction);
            if (world.mayInteract(playerEntity, blockpos) && playerEntity.mayUseItemAt(blockpos1, direction, itemstack)) {
                if (content == Fluids.EMPTY) {
                    BlockState blockstate1 = world.getBlockState(blockpos);
                    if (blockstate1.getBlock() instanceof BucketPickup) {
                        BucketPickup bucketpickup = (BucketPickup) blockstate1.getBlock();
                        ItemStack itemstack1 = bucketpickup.pickupBlock(world, blockpos, blockstate1);
                        if (!itemstack1.isEmpty()) {
                            playerEntity.awardStat(Stats.ITEM_USED.get(this));
                            bucketpickup.getPickupSound().ifPresent((p_150709_) -> {
                                playerEntity.playSound(p_150709_, 1.0F, 1.0F);
                            });
                            world.gameEvent(playerEntity, GameEvent.FLUID_PICKUP, blockpos);
                            ItemStack itemstack2 = ItemUtils.createFilledResult(itemstack, playerEntity, itemstack1);
                            if (!world.isClientSide) {
                                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer) playerEntity, itemstack1);
                            }

                            return InteractionResultHolder.sidedSuccess(itemstack2, world.isClientSide());
                        }
                    }

                    return InteractionResultHolder.fail(itemstack);
                } else {
                    BlockState blockstate = world.getBlockState(blockpos);
                    BlockPos blockpos2 = canBlockContainFluid(content, world, blockpos, blockstate) ? blockpos : blockpos1;
                    if (this.emptyBucket(content, playerEntity, world, blockpos2, blockhitresult)) {
                        this.checkExtraContent(world, itemstack, blockpos2);
                        if (playerEntity instanceof ServerPlayer) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) playerEntity, blockpos2, itemstack);
                        }

                        playerEntity.awardStat(Stats.ITEM_USED.get(this));
                        return InteractionResultHolder.sidedSuccess(getEmptySuccessItem(itemstack, playerEntity), world.isClientSide());
                    } else {
                        return InteractionResultHolder.fail(itemstack);
                    }
                }
            } else {
                return InteractionResultHolder.fail(itemstack);
            }
        }
    }

    protected ItemStack getEmptySuccessItem(ItemStack stack, Player playerEntity) {
        ItemStack empty = new ItemStack(this);
        empty.getOrCreateTag().putString(TAG_REGISTRY_NAME, "minecraft:empty");
        return !playerEntity.getAbilities().instabuild ? empty : stack;
    }

    public void checkExtraContent(Level world, ItemStack itemStack, BlockPos pos) {
    }

    public boolean emptyBucket(Fluid content, @Nullable Player playerEntity, Level world, BlockPos blockPos, @Nullable BlockHitResult blockRayTraceResult) {
        if (!(content instanceof FlowingFluid)) {
            return false;
        } else {
            BlockState blockstate = world.getBlockState(blockPos);
            Block block = blockstate.getBlock();
            Material material = blockstate.getMaterial();
            boolean flag = blockstate.canBeReplaced(content);
            boolean flag1 = blockstate.isAir() || flag || block instanceof LiquidBlockContainer && ((LiquidBlockContainer) block).canPlaceLiquid(world, blockPos, blockstate, content);
            if (!flag1) {
                return blockRayTraceResult != null && this.emptyBucket(content, playerEntity, world, blockRayTraceResult.getBlockPos().relative(blockRayTraceResult.getDirection()), (BlockHitResult) null);
            } else if (world.dimensionType().ultraWarm() && content.is(FluidTags.WATER)) {
                int i = blockPos.getX();
                int j = blockPos.getY();
                int k = blockPos.getZ();
                world.playSound(playerEntity, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++l) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                return true;
            } else if (block instanceof LiquidBlockContainer && ((LiquidBlockContainer) block).canPlaceLiquid(world, blockPos, blockstate, content)) {
                ((LiquidBlockContainer) block).placeLiquid(world, blockPos, blockstate, ((FlowingFluid) content).getSource(false));
                this.playEmptySound(content, playerEntity, world, blockPos);
                return true;
            } else {
                if (!world.isClientSide && flag && !material.isLiquid()) {
                    world.destroyBlock(blockPos, true);
                }

                if (!world.setBlock(blockPos, content.defaultFluidState().createLegacyBlock(), 11) && !blockstate.getFluidState().isSource()) {
                    return false;
                } else {
                    this.playEmptySound(content, playerEntity, world, blockPos);
                    return true;
                }
            }
        }
    }

    protected void playEmptySound(Fluid content, @Nullable Player playerEntity, Level iWorld, BlockPos pos) {
        SoundEvent soundevent = content.getAttributes().getEmptySound();
        if (soundevent == null)
            soundevent = content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        iWorld.playSound(playerEntity, pos, soundevent, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundTag nbt) {
        if (this.getClass() == UniversalBucketItem.class)
            return new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
        else
            return super.initCapabilities(stack, nbt);
    }

    private boolean canBlockContainFluid(Fluid content, Level worldIn, BlockPos posIn, BlockState blockstate) {
        return blockstate.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer) blockstate.getBlock()).canPlaceLiquid(worldIn, posIn, blockstate, content);
    }

    public void setFluids(List<Fluid> fluids) {
        this.fluids = fluids;
    }

    public Fluid get(String registryName) {
        return this.fluids.stream().filter(fluid -> Objects.requireNonNull(fluid.getRegistryName()).toString().equals(registryName)).findFirst().orElse(null);
    }

    public Fluid getFluid(ItemStack stack) {
        return stack.getOrCreateTag().getString(TAG_REGISTRY_NAME).isEmpty() ? Fluids.EMPTY : get(stack.getOrCreateTag().getString(TAG_REGISTRY_NAME));
    }
}
