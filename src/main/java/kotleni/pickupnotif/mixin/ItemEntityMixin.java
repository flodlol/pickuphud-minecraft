package kotleni.pickupnotif.mixin;

import kotleni.pickupnotif.ItemPickupCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class ItemEntityMixin {
    @Inject(
            method = "insertStack(Lnet/minecraft/item/ItemStack;)Z",
            at = @At("TAIL")
    )
    private void onItemPickup(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ItemPickupCallback.EVENT.invoker().onPickup(stack);
    }
}
