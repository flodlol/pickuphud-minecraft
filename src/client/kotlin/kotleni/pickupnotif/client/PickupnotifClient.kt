package kotleni.pickupnotif.client

import kotleni.pickupnotif.ItemPickupCallback
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack
import net.minecraft.text.Text

class PickupnotifClient : ClientModInitializer {
    private val client: MinecraftClient get() = MinecraftClient.getInstance();

    override fun onInitializeClient() {
        ItemPickupCallback.EVENT?.register { stack ->
            client.execute {
                onPickupItem(stack)
            }
        }
    }

    private fun onPickupItem(stack: ItemStack) {
        val message = "Picked up: ${stack.count}x ${stack.name.string}"
        client.inGameHud.chatHud.addMessage(Text.of(message))
    }
}
