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
import net.minecraft.util.math.vector.Vector3d;
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

    private static final DataParameter<String> FLUID_NAME = EntityDataManager.createKey(MooFluidEntity.class, DataSerializers.STRING);
    private static final DataParameter<Integer> DELAY = EntityDataManager.createKey(MooFluidEntity.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> CAN_BE_MILKED = EntityDataManager.createKey(MooFluidEntity.class, DataSerializers.BOOLEAN);

    private static final String TAG_FLUID = "FluidRegistryName";
    private static final String TAG_DELAY = "CurrentDelay";

    public MooFluidEntity(EntityType<? extends CowEntity> type, World worldIn) {
        super(type, worldIn);
    }

    public static AttributeModifierMap createAttr() {
        return CowEntity.func_233666_p_()
                .createMutableAttribute(Attributes.FOLLOW_RANGE, 16F)
                .createMutableAttribute(Attributes.MAX_HEALTH, 10F)
                .createMutableAttribute(Attributes.MOVEMENT_SPEED, 0.2F)
                .create();
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(FLUID_NAME, Objects.requireNonNull(Fluids.EMPTY.getRegistryName()).toString());
        this.dataManager.register(DELAY, 1000);
        this.dataManager.register(CAN_BE_MILKED, true);
    }

    @Override
    @ParametersAreNonnullByDefault
    public ILivingEntityData onInitialSpawn(IServerWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        if (!worldIn.isRemote()) {
            this.setFluid(Objects.requireNonNull(this.getRandomFluid().getRegistryName()).toString());
            if (this.getDelay() < 0) {
                this.dataManager.set(CAN_BE_MILKED, true);
            }
        }
        return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    @Override
    public void livingTick() {
        super.livingTick();

        if (this.getDelay() > 0 && !this.canBeMilked()) {
            this.decreaseDelay();
        } else {
            this.setCanBeMilked(true);
            this.setDelay(1000);
        }
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public ActionResultType applyPlayerInteraction(PlayerEntity player, Vector3d vec, Hand hand) {
        if (!this.world.isRemote) {
            if (this.canBeMilked()) {
                if (this.getFluid() != Fluids.EMPTY) {
                    if (hand == Hand.MAIN_HAND) {
                        if (player.getHeldItem(hand).getItem() == Items.BUCKET) {
                            ItemStack stack = FluidUtil.getFilledBucket(new FluidStack(this.getFluid(), 1000));
                            if (player.getHeldItem(hand).getCount() > 1) {
                                int slotID = player.inventory.getFirstEmptyStack();
                                if (slotID != -1) {
                                    player.inventory.mainInventory.set(slotID, stack);
                                    player.getHeldItem(hand).shrink(1);
                                    this.setCanBeMilked(false);
                                }
                            } else {
                                player.setHeldItem(hand, stack);
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
    public boolean canMateWith(@Nonnull AnimalEntity otherAnimal) {
        return false;
    }

    public boolean canBeMilked() {
        return this.dataManager.get(CAN_BE_MILKED);
    }

    public void setCanBeMilked(boolean value) {
        this.dataManager.set(CAN_BE_MILKED, value);
    }

    public void setFluid(String reg) {
        if (this.getFluid() == Fluids.EMPTY) {
            this.dataManager.set(FLUID_NAME, reg);
        }
    }

    public Fluid getFluid() {
        if (this.dataManager.get(FLUID_NAME).equals(Fluids.EMPTY.getRegistryName().toString()))
            return Fluids.EMPTY;

        return MooFluidReg.get(this.dataManager.get(FLUID_NAME));
    }

    public FluidStack getFluidStack(){
        return new FluidStack(this.getFluid(), FluidAttributes.BUCKET_VOLUME);
    }

    public int getDelay() {
        return this.dataManager.get(DELAY);
    }

    public void setDelay(int delay) {
        this.dataManager.set(DELAY, delay);
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
    public void writeAdditional(@Nonnull CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putInt(TAG_DELAY, this.getDelay());
        if(this.getFluid().getRegistryName() == null){
            compound.putString(TAG_FLUID, Objects.requireNonNull(Fluids.EMPTY.getRegistryName()).toString());
        }else {
            compound.putString(TAG_FLUID, Objects.requireNonNull(this.getFluid().getRegistryName()).toString());
        }
    }

    @Override
    public void readAdditional(@Nonnull CompoundNBT compound) {
        super.readAdditional(compound);
        this.setFluid(compound.getString(TAG_FLUID));
        this.setDelay(compound.getInt(TAG_DELAY));
    }
}
