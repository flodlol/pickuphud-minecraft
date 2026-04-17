package kotleni.pickuphud.ui.screens

import kotleni.pickuphud.ModConfig
import kotleni.pickupnotif.client.PickupMessage
import kotleni.pickupnotif.client.PickupsMessagesRenderer
import kotleni.pickuphud.settings.ModSetting
import kotleni.pickuphud.settings.ModSettingValue
import kotleni.pickuphud.settings.behaviorSettings
import kotleni.pickuphud.settings.renderingSettings
import kotleni.pickuphud.ui.widgets.IntSliderWidget
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.gui.widget.CyclingButtonWidget
import net.minecraft.client.gui.widget.TextWidget
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.Colors
import net.minecraft.util.Formatting

class ModSettingsScreen(private val parent: Screen?) : Screen(Text.literal("")) {
    private data class SettingsPage(
        val title: String,
        val settings: List<ModSetting<out Any>>,
    )

    private val pages = listOf(
        SettingsPage("Rendering", renderingSettings),
        SettingsPage("Behavior", behaviorSettings),
    )

    private var currentPageIndex = 0
    private val modConfigCopy: ModConfig = ModConfig.INSTANCE.copy()

    private var yOffset = 0

    private fun buildPreviewMessages(now: Long): List<PickupMessage> {
        val messages = mutableListOf<PickupMessage>(
            PickupMessage.Item(ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1), 1, 3, now),
            PickupMessage.Item(ItemStack(Items.COBBLESTONE, 32), 32, 128, now),
        )

        if (modConfigCopy.isDisplayExperienceOrb) {
            messages.add(PickupMessage.ExperienceOrb(9, 429, now))
        }

        return messages
    }

    private fun switchPage(delta: Int) {
        val lastIndex = pages.lastIndex
        currentPageIndex = (currentPageIndex + delta + pages.size) % pages.size
        currentPageIndex = currentPageIndex.coerceIn(0, lastIndex)
        clearAndInit()
    }

    private fun addSettingToggle(setting: ModSetting<Boolean>) {
        val rowY = 54 + yOffset

        addDrawableChild(TextWidget(
            this.width / 2 - 155,
            rowY,
            150,
            20,
            Text.literal(setting.title),
            textRenderer,
        ))

        addDrawableChild(
            CyclingButtonWidget.onOffBuilder(
                Text.literal("Enabled").formatted(Formatting.GREEN),
                Text.literal("Disabled").formatted(Formatting.RED),
                setting.getValue(modConfigCopy),
            )
                .omitKeyText()
                .build(
                    this.width / 2 + 5,
                    rowY,
                    150,
                    20,
                    Text.empty(),
                ) { _: CyclingButtonWidget<Boolean?>?, value: Boolean ->
                    setting.setValue(modConfigCopy, value)
                }
        )

        yOffset += 24
    }

    private fun addSettingIntField(setting: ModSetting<Int>) {
        val intValue = setting.value as ModSettingValue.ValueInt
        val rowY = 54 + yOffset

        addDrawableChild(TextWidget(
            this.width / 2 - 155,
            rowY,
            150,
            20,
            Text.literal(setting.title),
            textRenderer,
        ))

        addDrawableChild(
            IntSliderWidget(
                this.width / 2 + 5,
                rowY,
                150,
                20,
                Text.literal(setting.title),
                setting.getValue(modConfigCopy),
                intValue.min,
                intValue.max,
                onChangeValue = { newValue ->
                    setting.setValue(modConfigCopy, newValue)
                },
            )
        )

        yOffset += 24
    }

    private fun addSettingItem(setting: ModSetting<out Any>) {
        when (setting.value) {
            is ModSettingValue.ValueBoolean -> addSettingToggle(setting as ModSetting<Boolean>)
            is ModSettingValue.ValueInt -> addSettingIntField(setting as ModSetting<Int>)
            is ModSettingValue.ValueString -> {}
        }
    }

    override fun init() {
        yOffset = 0

        addDrawableChild(
            ButtonWidget.Builder(Text.literal("<")) {
                switchPage(-1)
            }
                .dimensions(this.width / 2 - 90, 26, 20, 20)
                .build()
        )

        addDrawableChild(
            ButtonWidget.Builder(Text.literal(">")) {
                switchPage(1)
            }
                .dimensions(this.width / 2 + 70, 26, 20, 20)
                .build()
        )

        pages[currentPageIndex].settings.forEach { setting ->
            addSettingItem(setting)
        }

        addDrawableChild(
            ButtonWidget.Builder(Text.literal("Tracked Items...")) {
                client?.setScreen(TrackedItemsScreen(this, modConfigCopy))
            }
                .dimensions(this.width / 2 - 60, this.height - 56, 120, 20)
                .build()
        )

        addDrawableChild(
            ButtonWidget.Builder(Text.literal("Cancel")) {
                close()
            }
                .dimensions(this.width / 2 - 205, this.height - 28, 200, 20)
                .build()
        )

        addDrawableChild(
            ButtonWidget.Builder(Text.literal("Save & Quit")) {
                ModConfig.INSTANCE.apply(modConfigCopy)
                ModConfig.save()
                close()
            }
                .dimensions(this.width / 2 + 5, this.height - 28, 200, 20)
                .build()
        )
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, deltaTicks: Float) {
        val now = System.currentTimeMillis()
        val previewMessages = buildPreviewMessages(now)
        PickupsMessagesRenderer.render(context, textRenderer, previewMessages, modConfigCopy)

        super.render(context, mouseX, mouseY, deltaTicks)

        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal("Pickup HUD Configuration"),
            this.width / 2,
            8,
            Colors.WHITE,
        )

        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal("Page ${currentPageIndex + 1}/${pages.size} - ${pages[currentPageIndex].title}"),
            this.width / 2,
            32,
            Colors.WHITE,
        )

        context.drawCenteredTextWithShadow(
            textRenderer,
            Text.literal("Tip: Change 'Open Tracked Items' in Controls -> Key Binds -> Pickup HUD"),
            this.width / 2,
            this.height - 68,
            0xD6D6D6,
        )
    }

    override fun close() {
        client?.setScreen(parent)
    }
}
