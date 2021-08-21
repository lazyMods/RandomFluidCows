package lazy.moofluids.client.model.item;

import lazy.moofluids.item.UniversalBucketItem;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public class UniversalBucketOverrides extends ItemOverrideList {

    @Override
    public IBakedModel resolve(@Nonnull IBakedModel model, ItemStack stack, ClientWorld world, LivingEntity livingEntity) {
        if (stack.getItem() instanceof UniversalBucketItem)
            return new UniversalBucketFinalModel(model, ((UniversalBucketItem)stack.getItem()).getFluid(stack));
        else return model;
    }
}