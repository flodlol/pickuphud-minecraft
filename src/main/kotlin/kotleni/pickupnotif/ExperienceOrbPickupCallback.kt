package kotleni.pickupnotif

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

fun interface ExperienceOrbPickupCallback {
    fun onPickup(player: PlayerEntity, experience: Int)

    companion object {
        @JvmField
        val EVENT: Event<ExperienceOrbPickupCallback?>? = EventFactory.createArrayBacked(ExperienceOrbPickupCallback::class.java) { listeners ->
            ExperienceOrbPickupCallback { player, experience ->
                for (listener in listeners) {
                    listener?.onPickup(player, experience)
                }
            }
        }
    }
}