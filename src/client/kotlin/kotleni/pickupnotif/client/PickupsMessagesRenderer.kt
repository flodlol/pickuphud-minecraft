package kotleni.pickupnotif.client

import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.util.Colors

object PickupsMessagesRenderer {
    private fun generateLine(message: PickupMessage): String {
        return when(message) {
            is PickupMessage.Item -> "${message.stack.itemName.string} +${message.increaseCount} (${message.totalCount})"
            is PickupMessage.ExperienceOrb -> "Experience +${message.increaseCount} (${message.totalCount})"
        };
    }

    fun render(drawContext: DrawContext, textRenderer: TextRenderer, messages: List<PickupMessage>) {
        messages.forEachIndexed { index, message ->
            if(System.currentTimeMillis() - message.createTime > 2000) return@forEachIndexed

            val line = generateLine(message)
            val margin = 16
            val padding = 6
            val width = textRenderer.getWidth(line)
            val height = textRenderer.fontHeight

            val x = drawContext.scaledWindowWidth - width - padding
            val y = drawContext.scaledWindowHeight - height - (margin * index) - padding - 6

            when(message) {
                is PickupMessage.Item -> drawContext.drawItem(message.stack, x - 20, y)
                is PickupMessage.ExperienceOrb -> drawContext.drawItem(Items.EXPERIENCE_BOTTLE.defaultStack, x - 20, y)
            }

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