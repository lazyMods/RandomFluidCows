package lazy.moofluids.entity;

import lazy.moofluids.utils.MooFluidReg;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.Random;

public class MooFluidEntity extends CowEntity {

    private static final DataParameter<String> FLUID_NAME = EntityDataManager.defineId(MooFluidEntity.class, DataSerializers.STRING);
    private static final DataParameter<Integer> DELAY = EntityDataManager.defineId(MooFluidEntity.class, DataSerializers.INT);
    private static final DataParameter<Boolean> CAN_BE_MILKED = EntityDataManager.defineId(MooFluidEntity.class, DataSerializers.BOOLEAN);

    private static final String TAG_FLUID = "FluidRegistryName";
    private static final String TAG_DELAY = "CurrentDelay";

    public MooFluidEntity(EntityType<? extends CowEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap createAttr() {
        return CowEntity.createAttributes()
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
    public ILivingEntityData finalizeSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
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
    public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
        if(!this.level.isClientSide) {
            if(this.canBeMilked()) {
                if(this.getFluid() != Fluids.EMPTY) {
                    if(hand == Hand.MAIN_HAND) {
                        if(player.getItemInHand(hand).getItem() == Items.BUCKET) {
                            ItemStack stack = FluidUtil.getFilledBucket(new FluidStack(this.getFluid(), 1000));
                            if(stack.isEmpty()) return ActionResultType.SUCCESS;
                            if(player.getItemInHand(hand).getCount() > 1) {
                                int slotID = player.inventory.getFreeSlot();
                                if(slotID != -1) {
                                    player.inventory.items.set(slotID, stack);
                                    player.getItemInHand(hand).shrink(1);
                                    this.setCanBeMilked(false);
                                }
                            } else {
                                player.setItemInHand(hand, stack);
                                this.setCanBeMilked(false);
                            }
                            return ActionResultType.SUCCESS;
                        }
                    }
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return this.getFluid() == null ? FluidStack.EMPTY.getDisplayName() : this.getFluidStack().getDisplayName();
    }

    @Override
    public boolean canMate(@Nonnull AnimalEntity otherAnimal) {
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
        int rndVal = MathHelper.nextInt(rnd, 0, MooFluidReg.getFluids().size() - 1);
        return MooFluidReg.getFluids().get(rndVal);
    }

    @Override
    public void addAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt(TAG_DELAY, this.getDelay());
        if(this.getFluid().getRegistryName() == null) {
            compound.putString(TAG_FLUID, Objects.requireNonNull(Fluids.EMPTY.getRegistryName()).toString());
        } else {
            compound.putString(TAG_FLUID, Objects.requireNonNull(this.getFluid().getRegistryName()).toString());
        }
    }

    @Override
    public void readAdditionalSaveData(@Nonnull CompoundNBT compound) {
        super.readAdditionalSaveData(compound);
        this.setFluid(compound.getString(TAG_FLUID));
        this.setDelay(compound.getInt(TAG_DELAY));
    }
}
