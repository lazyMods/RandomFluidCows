package lazy.moofluids.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class UniversalBucketItem extends Item {

    public static final String TAG_REGISTRY_NAME = "registryName";
    private List<Fluid> fluids;

    public UniversalBucketItem(List<Fluid> fluids) {
        super(new Item.Properties().tab(ItemGroup.TAB_TOOLS).stacksTo(16));
        this.fluids = fluids;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
        if (group != this.getItemCategory()) return;
        for (Fluid fluid : this.fluids) {
            ItemStack stack = new ItemStack(this);
            stack.setTag(new CompoundNBT());
            if (stack.getTag() != null)
                stack.getTag().putString(TAG_REGISTRY_NAME, Objects.requireNonNull(fluid.getRegistryName()).toString());
            items.add(stack);
        }
    }

    @Override
    @Nonnull
    public ITextComponent getName(ItemStack stack) {
        StringTextComponent textComponent = new StringTextComponent("");
        if (stack.getTag() != null && stack.getTag().contains(TAG_REGISTRY_NAME)) {
            textComponent.append("Universal Bucket of ");
            String renameName = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stack.getOrCreateTag().getString(TAG_REGISTRY_NAME))).getName().getString();
            textComponent.append(renameName);
        }
        return textComponent.getString().equals("") ? super.getName(stack) : textComponent;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        ItemStack itemstack = playerEntity.getItemInHand(hand);
        String fluidRegName = itemstack.getOrCreateTag().getString(TAG_REGISTRY_NAME);
        Fluid content = fluidRegName.isEmpty() ? Fluids.EMPTY : this.get(fluidRegName);
        RayTraceResult raytraceresult = getPlayerPOVHitResult(world, playerEntity, content == Fluids.EMPTY ? RayTraceContext.FluidMode.SOURCE_ONLY : RayTraceContext.FluidMode.NONE);
        ActionResult<ItemStack> ret = net.minecraftforge.event.ForgeEventFactory.onBucketUse(playerEntity, world, itemstack, raytraceresult);
        if (ret != null) return ret;
        if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
            return ActionResult.pass(itemstack);
        } else if (raytraceresult.getType() != RayTraceResult.Type.BLOCK) {
            return ActionResult.pass(itemstack);
        } else {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
            BlockPos blockpos = blockraytraceresult.getBlockPos();
            Direction direction = blockraytraceresult.getDirection();
            BlockPos blockpos1 = blockpos.relative(direction);
            if (world.mayInteract(playerEntity, blockpos) && playerEntity.mayUseItemAt(blockpos1, direction, itemstack)) {
                if (content == Fluids.EMPTY) {
                    BlockState blockstate1 = world.getBlockState(blockpos);
                    if (blockstate1.getBlock() instanceof IBucketPickupHandler) {
                        Fluid fluid = ((IBucketPickupHandler) blockstate1.getBlock()).takeLiquid(world, blockpos, blockstate1);
                        if (fluid != Fluids.EMPTY) {
                            playerEntity.awardStat(Stats.ITEM_USED.get(this));

                            SoundEvent soundevent = content.getAttributes().getFillSound();
                            if (soundevent == null)
                                soundevent = fluid.is(FluidTags.LAVA) ? SoundEvents.BUCKET_FILL_LAVA : SoundEvents.BUCKET_FILL;
                            playerEntity.playSound(soundevent, 1.0F, 1.0F);
                            ItemStack itemstack1 = DrinkHelper.createFilledResult(itemstack, playerEntity, new ItemStack(fluid.getBucket()));
                            if (!world.isClientSide) {
                                CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayerEntity) playerEntity, new ItemStack(fluid.getBucket()));
                            }

                            return ActionResult.sidedSuccess(itemstack1, world.isClientSide());
                        }
                    }

                    return ActionResult.fail(itemstack);
                } else {
                    BlockState blockstate = world.getBlockState(blockpos);
                    BlockPos blockpos2 = canBlockContainFluid(content, world, blockpos, blockstate) ? blockpos : blockpos1;
                    if (this.emptyBucket(content, playerEntity, world, blockpos2, blockraytraceresult)) {
                        this.checkExtraContent(world, itemstack, blockpos2);
                        if (playerEntity instanceof ServerPlayerEntity) {
                            CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) playerEntity, blockpos2, itemstack);
                        }

                        playerEntity.awardStat(Stats.ITEM_USED.get(this));
                        return ActionResult.sidedSuccess(this.getEmptySuccessItem(itemstack, playerEntity), world.isClientSide());
                    } else {
                        return ActionResult.fail(itemstack);
                    }
                }
            } else {
                return ActionResult.fail(itemstack);
            }
        }
    }

    protected ItemStack getEmptySuccessItem(ItemStack stack, PlayerEntity playerEntity) {
        ItemStack empty = new ItemStack(this);
        empty.getOrCreateTag().putString(TAG_REGISTRY_NAME, "minecraft:empty");
        return !playerEntity.abilities.instabuild ? empty : stack;
    }

    public void checkExtraContent(World world, ItemStack itemStack, BlockPos pos) {
    }

    public boolean emptyBucket(Fluid content, @Nullable PlayerEntity playerEntity, World world, BlockPos blockPos, @Nullable BlockRayTraceResult blockRayTraceResult) {
        if (!(content instanceof FlowingFluid)) {
            return false;
        } else {
            BlockState blockstate = world.getBlockState(blockPos);
            Block block = blockstate.getBlock();
            Material material = blockstate.getMaterial();
            boolean flag = blockstate.canBeReplaced(content);
            boolean flag1 = blockstate.isAir() || flag || block instanceof ILiquidContainer && ((ILiquidContainer) block).canPlaceLiquid(world, blockPos, blockstate, content);
            if (!flag1) {
                return blockRayTraceResult != null && this.emptyBucket(content, playerEntity, world, blockRayTraceResult.getBlockPos().relative(blockRayTraceResult.getDirection()), (BlockRayTraceResult) null);
            } else if (world.dimensionType().ultraWarm() && content.is(FluidTags.WATER)) {
                int i = blockPos.getX();
                int j = blockPos.getY();
                int k = blockPos.getZ();
                world.playSound(playerEntity, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                for (int l = 0; l < 8; ++l) {
                    world.addParticle(ParticleTypes.LARGE_SMOKE, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
                }

                return true;
            } else if (block instanceof ILiquidContainer && ((ILiquidContainer) block).canPlaceLiquid(world, blockPos, blockstate, content)) {
                ((ILiquidContainer) block).placeLiquid(world, blockPos, blockstate, ((FlowingFluid) content).getSource(false));
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

    protected void playEmptySound(Fluid content, @Nullable PlayerEntity playerEntity, IWorld iWorld, BlockPos pos) {
        SoundEvent soundevent = content.getAttributes().getEmptySound();
        if (soundevent == null)
            soundevent = content.is(FluidTags.LAVA) ? SoundEvents.BUCKET_EMPTY_LAVA : SoundEvents.BUCKET_EMPTY;
        iWorld.playSound(playerEntity, pos, soundevent, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }

    @Override
    public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, @Nullable net.minecraft.nbt.CompoundNBT nbt) {
        if (this.getClass() == UniversalBucketItem.class)
            return new net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper(stack);
        else
            return super.initCapabilities(stack, nbt);
    }

    private boolean canBlockContainFluid(Fluid content, World worldIn, BlockPos posIn, BlockState blockstate) {
        return blockstate.getBlock() instanceof ILiquidContainer && ((ILiquidContainer) blockstate.getBlock()).canPlaceLiquid(worldIn, posIn, blockstate, content);
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
