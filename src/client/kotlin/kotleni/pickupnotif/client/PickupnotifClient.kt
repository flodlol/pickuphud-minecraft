package kotleni.pickupnotif.client

import kotleni.pickupnotif.ItemPickupCallback
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.item.ItemStack

class PickupnotifClient : ClientModInitializer {
    private val client: MinecraftClient get() = MinecraftClient.getInstance();
    private val pickupsManager = PickupsManager();

    override fun onInitializeClient() {
        HudRenderCallback.EVENT.register { drawContext, tickCounter ->
            PickupsMessagesRenderer.render(drawContext, client.textRenderer, pickupsManager.allPickups)
        }

        ItemPickupCallback.EVENT?.register { stack ->
            client.execute {
                onPickupItem(stack)
            }
        }
    }

    private fun onPickupItem(stack: ItemStack) {
        val totalCount = client.player?.inventory
            ?.toList()
            ?.filter { it.itemName == stack.itemName }
            ?.map { it.count }
            ?.reduce { a, b -> a + b } ?: -1

        pickupsManager.addPickup(stack, totalCount);
    }
}
