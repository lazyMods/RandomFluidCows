package lazy.moofluids.inventory.container;

import lazy.moofluids.Setup;
import lazy.moofluids.tile.AutoMilkerTile;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class AutoMilkerContainer extends AbstractContainerMenu {

    private final ContainerData data;

    public AutoMilkerContainer(int id, Inventory inventory, IItemHandler tileInv, ContainerData data) {
        super(Setup.AUTO_MILKER_CONTAINER.get(), id);

        this.data = data;

        this.addSlot(new SlotItemHandler(tileInv, 0, 26, 36){
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return stack.getItem() == Items.BUCKET;
            }
        });
        this.addSlot(new SlotItemHandler(tileInv, 1, 134, 36){
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return stack.getItem() instanceof BucketItem && ((BucketItem)stack.getItem()).getFluid() != Fluids.EMPTY;
            }
        });

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(inventory, k, 8 + k * 18, 142));
        }

        this.addDataSlots(data);
    }

    public int getFluidAmount(){
        return this.data.get(0);
    }

    public int getCapacity(){
        return this.data.get(1);
    }

    public boolean isEmpty(){
        return this.data.get(2) == 1;
    }

    public int getFluidColor(){
        return this.data.get(3);
    }

    public AutoMilkerContainer(int id, Inventory inventory) {
        this(id, inventory, new ItemStackHandler(AutoMilkerTile.INV_SIZE), new SimpleContainerData(AutoMilkerTile.DATA_SIZE));
    }

    @Override
    public boolean stillValid(@Nonnull Player playerIn) {
        return true;
    }
}
