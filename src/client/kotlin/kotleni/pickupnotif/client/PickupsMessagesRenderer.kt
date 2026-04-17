package kotleni.pickupnotif.client

import kotleni.pickuphud.ModConfig
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import kotlin.math.max

object PickupsMessagesRenderer {
    private fun colorInt(red: Int, green: Int, blue: Int, alpha: Int): Int {
        val a = alpha.coerceIn(0, 255)
        val r = red.coerceIn(0, 255)
        val g = green.coerceIn(0, 255)
        val b = blue.coerceIn(0, 255)
        return (a shl 24) or (r shl 16) or (g shl 8) or b
    }

    private fun generateLine(message: PickupMessage, config: ModConfig): String {
        return when (message) {
            is PickupMessage.Item -> {
                val stacksCount = (message.totalCount.toDouble() / message.stack.item.maxCount).toInt()
                val partialStackCount = message.totalCount - (stacksCount * message.stack.item.maxCount)
                val totalCount = if (config.isDisplayTotalCountInStacks && stacksCount > 0) {
                    "$stacksCount ${if (stacksCount == 1) "stack" else "stacks"}${if (partialStackCount == 0) "" else " + $partialStackCount"}"
                } else {
                    message.totalCount.toString()
                }

                "${message.stack.name.string} +${message.increaseCount} ($totalCount)"
            }

            is PickupMessage.ExperienceOrb -> "Experience +${message.increaseCount} (${message.totalCount})"
        }
    }

    private fun getRarityFormatting(message: PickupMessage): Formatting {
        return when (message) {
            is PickupMessage.Item -> message.stack.rarity.formatting
            is PickupMessage.ExperienceOrb -> Formatting.AQUA
        } ?: Formatting.WHITE
    }

    fun render(
        drawContext: DrawContext,
        textRenderer: TextRenderer,
        messages: List<PickupMessage>,
        config: ModConfig = ModConfig.INSTANCE,
    ) {
        var yOffset = 0
        var renderedCount = 0

        for (message in messages) {
            if (System.currentTimeMillis() - message.createTime > config.messageTime) continue

            if (renderedCount >= config.maxMessagesOnScreen) {
                message.createTime = System.currentTimeMillis()
                continue
            }

            val padding = config.messagePadding
            val gap = config.gapBetweenMessages

            val textColor = -1
            val backgroundColor = colorInt(
                config.messageBackgroundShade,
                config.messageBackgroundShade,
                config.messageBackgroundShade,
                config.messageBackgroundOpacity,
            )
            val textBackgroundColor = colorInt(0, 0, 0, config.textBackgroundOpacity)

            val renderIcon = config.isRenderItemIcon
            val iconScale = 0.75f

            val line = generateLine(message, config)
            val textWidth = textRenderer.getWidth(line)
            val textHeight = textRenderer.fontHeight

            val baseItemSize = 16
            val scaledItemSize = (baseItemSize * iconScale).toInt()
            val iconTextGap = 4
            val iconAreaWidth = if (renderIcon) scaledItemSize + iconTextGap else 0

            val contentHeight = max(textHeight, if (renderIcon) scaledItemSize else 0)
            val backgroundHeight = contentHeight + (padding * 2)
            val backgroundWidth = textWidth + iconAreaWidth + (padding * 2)

            val maxX = max(drawContext.scaledWindowWidth - backgroundWidth, 0)
            val maxY = max(drawContext.scaledWindowHeight - backgroundHeight, 0)

            val horizontalOffset = max(config.hudOffsetX, 0)
            val verticalOffset = max(config.hudOffsetY, 0)

            val rawX = if (config.isHudRightAligned) {
                drawContext.scaledWindowWidth - backgroundWidth - horizontalOffset
            } else {
                horizontalOffset
            }

            val rawY = if (config.isHudBottomAligned) {
                drawContext.scaledWindowHeight - backgroundHeight - verticalOffset - yOffset
            } else {
                verticalOffset + yOffset
            }

            val backgroundX = rawX.coerceIn(0, maxX)
            val backgroundY = rawY.coerceIn(0, maxY)

            if (config.messageBackgroundOpacity > 0) {
                drawContext.fill(
                    backgroundX,
                    backgroundY,
                    backgroundX + backgroundWidth,
                    backgroundY + backgroundHeight,
                    backgroundColor,
                )
            }

            if (renderIcon) {
                val iconX = backgroundX + padding
                val iconY = backgroundY + (backgroundHeight / 2) - (scaledItemSize / 2)
                val matrices = drawContext.matrices

                matrices.pushMatrix()
                matrices.translate(iconX.toFloat(), iconY.toFloat())
                matrices.scale(iconScale, iconScale)

                when (message) {
                    is PickupMessage.Item -> drawContext.drawItem(message.stack, 0, 0)
                    is PickupMessage.ExperienceOrb -> drawContext.drawItem(Items.EXPERIENCE_BOTTLE.defaultStack, 0, 0)
                }

                matrices.popMatrix()
            }

            val text = if (config.isColorizeTextByRarity) {
                Text.literal(line).formatted(getRarityFormatting(message))
            } else {
                Text.literal(line)
            }

            val textX = backgroundX + padding + iconAreaWidth
            val textY = backgroundY + (backgroundHeight / 2) - (textHeight / 2)

            if (config.textBackgroundOpacity > 0) {
                drawContext.fill(textX - 1, textY - 1, textX + textWidth + 1, textY + textHeight + 1, textBackgroundColor)
            }

            drawContext.drawText(textRenderer, text, textX, textY, textColor, true)

            yOffset += backgroundHeight + gap
            renderedCount++
        }
    }
}
