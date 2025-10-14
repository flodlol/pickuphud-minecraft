package kotleni.pickupnotif.client

import kotleni.pickuphud.ModConfig
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.boss.BossBar
import net.minecraft.item.Item
import net.minecraft.item.Items
import net.minecraft.registry.Registries
import net.minecraft.text.StringVisitable
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.text.TextColor
import net.minecraft.util.Colors
import net.minecraft.util.Formatting
import net.minecraft.util.Rarity

object PickupsMessagesRenderer {
    private fun generateLine(message: PickupMessage): String {
        return when(message) {
            is PickupMessage.Item -> {
                val stacksCount = (message.totalCount.toDouble() / message.stack.item.maxCount).toInt()
                val partialStackCount = message.totalCount - (stacksCount * message.stack.item.maxCount)
                val totalCount = if(ModConfig.INSTANCE.isDisplayTotalCountInStacks && stacksCount > 0)
                    "$stacksCount ${if(stacksCount == 1) "stack" else "stacks"}${if(partialStackCount == 0) "" else " + $partialStackCount"}"
                else
                    message.totalCount.toString()

                return "${message.stack.itemName.string} +${message.increaseCount} ($totalCount)"
            }
            is PickupMessage.ExperienceOrb -> "Experience +${message.increaseCount} (${message.totalCount})"
        };
    }

    private fun getRarityColor(message: PickupMessage): Int {
        return when(message) {
            is PickupMessage.Item -> when(message.stack.rarity) {
                Rarity.COMMON -> Colors.WHITE
                Rarity.UNCOMMON -> Colors.YELLOW
                Rarity.RARE -> Colors.CYAN
                Rarity.EPIC -> Colors.PURPLE
                else -> Colors.WHITE
            }
            is PickupMessage.ExperienceOrb -> Colors.WHITE
        } ?: Colors.WHITE
    }

    private fun getRarityFormatting(message: PickupMessage): Formatting {
        return when(message) {
            is PickupMessage.Item -> message.stack.rarity.formatting
            is PickupMessage.ExperienceOrb -> Formatting.WHITE
        } ?: Formatting.WHITE
    }

    fun render(drawContext: DrawContext, textRenderer: TextRenderer, messages: List<PickupMessage>) {
        var renderedCount = 0
        messages.forEach { message ->
            if(System.currentTimeMillis() - message.createTime > ModConfig.INSTANCE.messageTime) return@forEach

            if(renderedCount >= ModConfig.INSTANCE.maxMessagesOnScreen) {
                // Reset timer for not displayed messages
                message.createTime = System.currentTimeMillis()
                return@forEach
            }

            val line = generateLine(message)
            val margin = 16
            val padding = 6
            val width = textRenderer.getWidth(line)
            val height = textRenderer.fontHeight

            val x = drawContext.scaledWindowWidth - width - padding
            val y = drawContext.scaledWindowHeight - height - (margin * renderedCount) - padding - 6

            if(ModConfig.INSTANCE.isRenderItemIcon)
            when(message) {
                is PickupMessage.Item -> drawContext.drawItem(message.stack, x - 20, y)
                is PickupMessage.ExperienceOrb -> drawContext.drawItem(Items.EXPERIENCE_BOTTLE.defaultStack, x - 20, y)
            }

            val text = if(ModConfig.INSTANCE.isColorizeTextByRarity)
                Text.literal(line).formatted(getRarityFormatting(message))
            else
                Text.literal(line)

            drawContext.drawText(
                textRenderer,
                text,
                x,
                y + (height / 2),
                Colors.WHITE,
                false
            )

            renderedCount += 1
        }
    }
}