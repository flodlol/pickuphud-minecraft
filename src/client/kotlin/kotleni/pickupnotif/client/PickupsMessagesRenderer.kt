package kotleni.pickupnotif.client

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Colors

object PickupsMessagesRenderer {
    fun render(drawContext: DrawContext, textRenderer: TextRenderer, messages: List<PickupMessage>) {
        messages.forEachIndexed { index, message ->
            if(System.currentTimeMillis() - message.createTime > 2000) return@forEachIndexed

            val line = "${message.itemName} +${message.itemCount} (${message.itemsTotal})"
            val margin = 16
            val padding = 6
            val width = textRenderer.getWidth(line)
            val height = textRenderer.fontHeight

            val x = drawContext.scaledWindowWidth - width - padding
            val y = drawContext.scaledWindowHeight - height - (margin * index) - padding - 6

            drawContext.drawItem(message.stack, x - 20, y)
            drawContext.drawText(
                textRenderer,
                line,
                x,
                y + (height / 2),
                Colors.WHITE,
                false
            );
        }
    }
}