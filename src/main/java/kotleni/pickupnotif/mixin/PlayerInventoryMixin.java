package kotleni.pickupnotif.mixin;

import kotleni.pickupnotif.ItemPickupCallback;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin {
    @Inject(
            method = "insertStack(ILnet/minecraft/item/ItemStack;)Z",
            at = @At("RETURN")
    )
    private void insertStack(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        ItemPickupCallback.EVENT.invoker().onPickup(stack);
    }
}
