package lazy.moofluids.client.model.item;

import lazy.moofluids.item.UniversalBucketItem;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class UniversalBucketOverrides extends ItemOverrides {

    @Override
    public BakedModel resolve(@Nonnull BakedModel model, ItemStack stack, ClientLevel world, LivingEntity livingEntity, int i) {
        if (stack.getItem() instanceof UniversalBucketItem)
            return new UniversalBucketFinalModel(model, ((UniversalBucketItem) stack.getItem()).getFluid(stack));
        else return model;
    }
}