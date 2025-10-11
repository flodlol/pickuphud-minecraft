package kotleni.pickupnotif.client

import kotleni.pickupnotif.ItemPickupCallback
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder
import net.minecraft.client.gui.widget.ClickableWidget
import net.minecraft.client.gui.widget.Widget
import net.minecraft.client.render.Tessellator
import net.minecraft.entity.boss.BossBar
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Colors
import java.util.function.Consumer
//
//class TestWidget : Widget {
//    override fun setX(x: Int) {
//
//    }
//
//    override fun setY(y: Int) {
//
//    }
//
//    override fun getX(): Int {
//
//    }
//
//    override fun getY(): Int {
//
//    }
//
//    override fun getWidth(): Int {
//
//    }
//
//    override fun getHeight(): Int {
//
//    }
//
//    override fun forEachChild(consumer: Consumer<ClickableWidget?>?) {
//
//    }
//}

data class PickupMessage(
    val itemName: String,
    var itemCount: Int,
    var itemsTotal: Int,
    var createTime: Long,
    val stack: ItemStack
)

class PickupnotifClient : ClientModInitializer {
    private val client: MinecraftClient get() = MinecraftClient.getInstance();
    private var pickupMessages: ArrayList<PickupMessage> = arrayListOf();

    override fun onInitializeClient() {
        HudRenderCallback.EVENT.register { drawContext, tickCounter ->
            pickupMessages.forEachIndexed { index, message ->
                if(System.currentTimeMillis() - message.createTime > 2000) return@forEachIndexed

                val line = "${message.itemName} +${message.itemCount} (${message.itemsTotal})"
                val margin = 16
                val padding = 6
                val width = client.inGameHud.textRenderer.getWidth(line)
                val height = client.inGameHud.textRenderer.fontHeight

                val x = drawContext.scaledWindowWidth - width - padding
                val y = drawContext.scaledWindowHeight - height - (margin * index) - padding - 6

                drawContext.drawItem(message.stack, x - 20, y)
                drawContext.drawText(
                    client.inGameHud.textRenderer,
                    line,
                    x,
                    y + (height / 2),
                    Colors.WHITE,
                    false
                );
            }
        }
        ItemPickupCallback.EVENT?.register { stack ->
            client.execute {
                onPickupItem(stack)
            }
        }
    }

    private fun onPickupItem(stack: ItemStack) {
        pickupMessages.removeIf { System.currentTimeMillis() - it.createTime >= 2000 }

        val totalCount = client.player?.inventory
            ?.toList()
            ?.filter { it.itemName == stack.itemName }
            ?.map { it.count }
            ?.reduce { a, b -> a + b } ?: -1

        val prevMessage = pickupMessages.find { it.itemName == stack.itemName.string }
        if(prevMessage != null) {
            prevMessage.itemCount += stack.count;
            prevMessage.createTime = System.currentTimeMillis();
        } else {
            pickupMessages.add(PickupMessage(
                stack.name.string,
                stack.count,
                totalCount,
                System.currentTimeMillis(),
                stack
            ))
        }
    }
}
