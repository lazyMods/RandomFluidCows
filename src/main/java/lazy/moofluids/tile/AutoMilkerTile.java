package lazy.moofluids.tile;

import com.google.common.base.Preconditions;
import lazy.moofluids.Setup;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.inventory.container.AutoMilkerContainer;
import lazy.moofluids.utils.FluidColorFromTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
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

public class AutoMilkerTile extends BlockEntity implements MenuProvider {

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
    public ContainerData data = new ContainerData() {
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
                    if (color == -1 && FluidColorFromTexture.COLORS.containsKey(storage.getFluid().getFluid()))
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

    public AutoMilkerTile(BlockPos pos, BlockState state) {
        super(Setup.AUTO_MILKER_TYPE.get(), pos, state);
    }

    public void tick(Level level, BlockPos pos, BlockState state, AutoMilkerTile tile) {
        Preconditions.checkNotNull(this.level);
        if (!level.isClientSide) {
            this.setFacing(state);
            if (!this.getMooFluidInSpace(level, pos).isEmpty()) {
                this.increaseTimer();
                if (this.finished()) {
                    for (MooFluidEntity mooFluid : this.getMooFluidInSpace(level, pos)) {
                        int remainder = this.storage.fill(mooFluid.getFluidStack(), FluidAction.SIMULATE);
                        if (remainder != 0 && mooFluid.canBeMilked()) {
                            level.playSound(null, pos, SoundEvents.COW_MILK, SoundSource.AMBIENT, 1f, 1f);
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
            if (hasEmptyBucket && outputIsEmpty) {
                if (!this.storage.isEmpty()) {
                    this.tileInv.extractItem(0, 1, false);
                    this.tileInv.insertItem(1, FluidUtil.getFilledBucket(this.storage.getFluid()), false);
                    this.storage.drain(FluidAttributes.BUCKET_VOLUME, FluidAction.EXECUTE);
                }
            }
        }
    }

    public List<MooFluidEntity> getMooFluidInSpace(@Nonnull Level level, BlockPos pos) {
        return level.getEntitiesOfClass(MooFluidEntity.class, new AABB(this.getFacingPos(pos)));
    }

    private void setFacing(BlockState state) {
        if (this.facing == null && state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            this.facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
    }

    public int isEmpty() {
        return this.storage.isEmpty() ? 1 : 0;
    }

    private BlockPos getFacingPos(BlockPos pos) {
        return pos.offset(this.facing.getNormal());
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
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.timer = nbt.getInt(TAG_TIME);
        this.storage.readFromNBT(nbt);
        this.tileInv.deserializeNBT(nbt.getCompound(TAG_INV));
    }

    @Override
    @Nonnull
    @ParametersAreNonnullByDefault
    public CompoundTag save(CompoundTag compound) {
        CompoundTag nbt = super.save(compound);
        nbt.putInt(TAG_TIME, this.timer);
        this.storage.writeToNBT(compound);
        nbt.put(TAG_INV, this.tileInv.serializeNBT());
        return nbt;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!this.isRemoved() && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side != this.facing) {
            return this.fluidCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.fluidCap.invalidate();
    }

    @Override
    @Nonnull
    public Component getDisplayName() {
        return new TextComponent("Auto Milker");
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
        return new AutoMilkerContainer(windowId, playerInventory, this.tileInv, this.data);
    }
}
