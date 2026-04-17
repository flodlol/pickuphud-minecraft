package kotleni.pickupnotif.mixin;

import kotleni.pickupnotif.ExperienceOrbPickupCallback;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntity.class)
public abstract class ExperienceOrbEntityMixin {
    @Inject(
            method = "onPlayerCollision", at = @At(
            value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;sendPickup(Lnet/minecraft/entity/Entity;I)V"
    )
    )
    private void onPickup(PlayerEntity player, CallbackInfo ci) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            ExperienceOrbEntity self = (ExperienceOrbEntity) (Object) this;
            ExperienceOrbPickupCallback.EVENT.invoker().onPickup(serverPlayer, self.getValue());
        }
    }
}
