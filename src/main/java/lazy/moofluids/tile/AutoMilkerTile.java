package lazy.moofluids.tile;

import com.google.common.base.Preconditions;
import lazy.moofluids.Setup;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.inventory.container.AutoMilkerContainer;
import lazy.moofluids.utils.FluidColorFromTexture;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

public class AutoMilkerTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {

    public static final String TAG_TIME = "CurrentTime";
    private int timer;
    private final int finishTime = 100;

    private final FluidTank storage = new FluidTank(10000);
    private final LazyOptional<IFluidHandler> fluidCap = LazyOptional.of(() -> storage);

    public static final String TAG_INV = "AutoMilkerInventory";
    public static final int INV_SIZE = 2;
    private final ItemStackHandler tileInv = new ItemStackHandler(2) {
        @Override
        protected int getStackLimit(int slot, @Nonnull ItemStack stack) {
            return slot == 0 ? 16 : 1;
        }
    };

    private Direction facing;

    public static final int DATA_SIZE = 4;
    public IIntArray data = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return storage.getFluidAmount();
                case 1:
                    return storage.getCapacity();
                case 2:
                    return isEmpty();
                case 3:
                    int color = storage.getFluid().getFluid().getAttributes().getColor();
                    if(color == -1 && FluidColorFromTexture.COLORS.containsKey(storage.getFluid().getFluid()))
                        color = FluidColorFromTexture.COLORS.get(storage.getFluid().getFluid());
                    return color;
                default:
                    return 69;
            }
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return DATA_SIZE;
        }
    };

    public AutoMilkerTile() {
        super(Setup.AUTO_MILKER_TYPE.get());
    }

    @Override
    public void tick() {
        Preconditions.checkNotNull(this.level);
        if(!this.level.isClientSide) {
            this.setFacing();
            if(!this.getMooFluidInSpace(this.level).isEmpty()) {
                this.increaseTimer();
                if(this.finished()) {
                    for (MooFluidEntity mooFluid : this.getMooFluidInSpace(this.level)) {
                        int remainder = this.storage.fill(mooFluid.getFluidStack(), FluidAction.SIMULATE);
                        if(remainder != 0 && mooFluid.canBeMilked()) {
                            this.level.playSound(null, this.worldPosition, SoundEvents.COW_MILK, SoundCategory.AMBIENT, 1f, 1f);
                            mooFluid.setCanBeMilked(false);
                            mooFluid.setDelay(1000);
                            this.storage.fill(mooFluid.getFluidStack(), FluidAction.EXECUTE);
                        }
                    }
                    this.resetTimer();
                }
            }

            boolean hasEmptyBucket = this.tileInv.getStackInSlot(0).getItem() == Items.BUCKET;
            boolean outputIsEmpty = this.tileInv.getStackInSlot(1).isEmpty();
            if(hasEmptyBucket && outputIsEmpty) {
                if(!this.storage.isEmpty()) {
                    this.tileInv.extractItem(0, 1, false);
                    this.tileInv.insertItem(1, FluidUtil.getFilledBucket(this.storage.getFluid()), false);
                    this.storage.drain(FluidAttributes.BUCKET_VOLUME, FluidAction.EXECUTE);
                }
            }
        }
    }

    public List<MooFluidEntity> getMooFluidInSpace(@Nonnull World level) {
        return level.getEntitiesOfClass(MooFluidEntity.class, new AxisAlignedBB(this.getFacingPos()));
    }

    private void setFacing() {
        if(this.facing == null && this.getBlockState().hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            this.facing = this.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
    }

    public int isEmpty() {
        return this.storage.isEmpty() ? 1 : 0;
    }

    private BlockPos getFacingPos() {
        return this.worldPosition.offset(this.facing.getNormal());
    }

    private void increaseTimer() {
        this.timer++;
        this.setChanged();

    }

    private void resetTimer() {
        this.timer = 0;
        this.setChanged();
    }

    private boolean finished() {
        return this.timer >= this.finishTime;
    }

    public int getCapacity() {
        return this.storage.getCapacity();
    }

    public int getFluidAmount() {
        return this.storage.getFluidAmount();
    }

    public int getTimer() {
        return this.timer;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        this.timer = nbt.getInt(TAG_TIME);
        this.storage.readFromNBT(nbt);
        this.tileInv.deserializeNBT(nbt.getCompound(TAG_INV));
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public CompoundNBT save(CompoundNBT compound) {
        CompoundNBT nbt = super.save(compound);
        nbt.putInt(TAG_TIME, this.timer);
        this.storage.writeToNBT(compound);
        nbt.put(TAG_INV, this.tileInv.serializeNBT());
        return nbt;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(!this.isRemoved() && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != this.facing) {
            return this.fluidCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        this.fluidCap.invalidate();
    }

    @Override
    @Nonnull
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Auto Milker");
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new AutoMilkerContainer(windowId, playerInventory, this.tileInv, this.data);
    }
}
