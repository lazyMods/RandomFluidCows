package lazy.moofluids.entity;

import lazy.moofluids.utils.MooFluidReg;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.util.Mth;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

public class MooFluidEntity extends Cow {

    private static final EntityDataAccessor<String> FLUID_NAME = SynchedEntityData.defineId(MooFluidEntity.class, EntityDataSerializers.STRING);
    private static final EntityDataAccessor<Integer> DELAY = SynchedEntityData.defineId(MooFluidEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> CAN_BE_MILKED = SynchedEntityData.defineId(MooFluidEntity.class, EntityDataSerializers.BOOLEAN);

    private static final String TAG_FLUID = "FluidRegistryName";
    private static final String TAG_DELAY = "CurrentDelay";

    public MooFluidEntity(EntityType<? extends Cow> type, Level worldIn) {
        super(type, worldIn);
    }

    public static AttributeSupplier createAttr() {
        return Cow.createAttributes()
                .add(Attributes.FOLLOW_RANGE, 16F)
                .add(Attributes.MAX_HEALTH, 10F)
                .add(Attributes.MOVEMENT_SPEED, 0.2F)
                .build();
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(FLUID_NAME, Objects.requireNonNull(Fluids.EMPTY.getRegistryName()).toString());
        this.entityData.define(DELAY, 1000);
        this.entityData.define(CAN_BE_MILKED, true);
    }

    @Override
    @ParametersAreNonnullByDefault
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if(!worldIn.isClientSide()) {
            this.setFluid(Objects.requireNonNull(this.getRandomFluid().getRegistryName()).toString());
            if(this.getDelay() < 0) {
                this.entityData.set(CAN_BE_MILKED, true);
            }
        }
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if(this.getDelay() > 0 && !this.canBeMilked()) {
            this.decreaseDelay();
        } else {
            this.setCanBeMilked(true);
            this.setDelay(1000);
        }
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if(!this.level.isClientSide) {
            if(this.canBeMilked()) {
                if(this.getFluid() != Fluids.EMPTY) {
                    if(hand == InteractionHand.MAIN_HAND) {
                        if(player.getItemInHand(hand).getItem() == Items.BUCKET) {
                            ItemStack stack = FluidUtil.getFilledBucket(new FluidStack(this.getFluid(), 1000));
                            if(player.getItemInHand(hand).getCount() > 1) {
                                int slotID = player.getInventory().getFreeSlot();
                                if(slotID != -1) {
                                    player.getInventory().items.set(slotID, stack);
                                    player.getItemInHand(hand).shrink(1);
                                    this.setCanBeMilked(false);
                                }
                            } else {
                                player.setItemInHand(hand, stack);
                                this.setCanBeMilked(false);
                            }
                            return InteractionResult.SUCCESS;
                        }
                    }
                }
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return this.getFluid() == null ? FluidStack.EMPTY.getDisplayName() : this.getFluidStack().getDisplayName();
    }

    @Override
    public boolean canMate(@Nonnull Animal otherAnimal) {
        return false;
    }

    public boolean canBeMilked() {
        return this.entityData.get(CAN_BE_MILKED);
    }

    public void setCanBeMilked(boolean value) {
        this.entityData.set(CAN_BE_MILKED, value);
    }

    public void setFluid(String reg) {
        if(this.getFluid() == Fluids.EMPTY) {
            this.entityData.set(FLUID_NAME, reg);
        }
    }

    public Fluid getFluid() {
        if(this.entityData.get(FLUID_NAME).equals(Objects.requireNonNull(Fluids.EMPTY.getRegistryName()).toString()))
            return Fluids.EMPTY;

        return MooFluidReg.get(this.entityData.get(FLUID_NAME));
    }

    public FluidStack getFluidStack() {
        return new FluidStack(this.getFluid(), FluidAttributes.BUCKET_VOLUME);
    }

    public int getDelay() {
        return this.entityData.get(DELAY);
    }

    public void setDelay(int delay) {
        this.entityData.set(DELAY, delay);
    }

    public void decreaseDelay() {
        this.setDelay(this.getDelay() - 1);
    }

    public Fluid getRandomFluid() {
        Random rnd = new Random();
        int rndVal = Mth.nextInt(rnd, 0, MooFluidReg.getFluids().size() - 1);
        return MooFluidReg.getFluids().get(rndVal);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(TAG_DELAY, this.getDelay());
        if(this.getFluid().getRegistryName() == null) {
            compound.putString(TAG_FLUID, Objects.requireNonNull(Fluids.EMPTY.getRegistryName()).toString());
        } else {
            compound.putString(TAG_FLUID, Objects.requireNonNull(this.getFluid().getRegistryName()).toString());
        }
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setFluid(compound.getString(TAG_FLUID));
        this.setDelay(compound.getInt(TAG_DELAY));
    }
}
