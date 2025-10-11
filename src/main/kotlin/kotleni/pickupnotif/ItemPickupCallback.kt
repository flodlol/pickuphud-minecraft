package kotleni.pickupnotif

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

fun interface ItemPickupCallback {
    fun onPickup(player: PlayerEntity, stack: ItemStack)

    companion object {
        @JvmField
        val EVENT: Event<ItemPickupCallback?>? = EventFactory.createArrayBacked(ItemPickupCallback::class.java) { listeners ->
            ItemPickupCallback { player, stack ->
                for (listener in listeners) {
                    listener?.onPickup(player, stack)
                }
            }
        }
    }
}