package kotleni.pickupnotif.client

import kotleni.pickuphud.ModConfig
import kotleni.pickuphud.ui.screens.TrackedItemsScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW

class PickupnotifClient : ClientModInitializer {
    private val client: MinecraftClient get() = MinecraftClient.getInstance()
    private val pickupsManager = PickupsManager()
    private val pickupsTracker by lazy { PickupsTracker(client, pickupsManager) }
    private lateinit var openTrackedItemsKeybind: KeyBinding

    override fun onInitializeClient() {
        ModConfig.load()
        openTrackedItemsKeybind = KeyBindingHelper.registerKeyBinding(
            KeyBinding(
                "key.pickuphud.open_tracked_items",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                KeyBinding.Category.create(Identifier.of("pickuphud", "pickuphud")),
            )
        )

        HudRenderCallback.EVENT.register { drawContext, tickCounter ->
            PickupsMessagesRenderer.render(drawContext, client.textRenderer, pickupsManager.allPickups, ModConfig.INSTANCE)
        }

        ClientTickEvents.END_CLIENT_TICK.register {
            pickupsTracker.tick()
            while (openTrackedItemsKeybind.wasPressed()) {
                val currentConfigCopy = ModConfig.INSTANCE.copy()
                client.setScreen(TrackedItemsScreen(client.currentScreen, currentConfigCopy))
            }
        }
    }
}
