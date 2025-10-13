package kotleni.pickuphud

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.clothconfig2.api.ConfigBuilder
import me.shedaniel.clothconfig2.api.ConfigCategory
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

sealed class ModSettingValue(
) {
    data class ValueBoolean(
        val defaultValue: Boolean,
    ): ModSettingValue()

    data class ValueInt(
        val defaultValue: Int,
        val min: Int,
        val max: Int,
    ): ModSettingValue()
}

data class ModSetting<T>(
    val title: String,
    val description: String,
    val value: ModSettingValue,
    val getValue: (modConfig: ModConfig) -> T,
    val setValue: (modConfig: ModConfig, value: T) -> Unit
)

private val renderingSettings = listOf(
    ModSetting(
        title = "Item icon",
        description = "Render item icon near to the message item.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> return@ModSetting cfg.isRenderItemIcon },
        setValue = { cfg, value -> cfg.isRenderItemIcon = value }
    ),
)

private val behaviorSettings = listOf(
    ModSetting(
        title = "Track experience orbs",
        description = "Track and display messages when pickup experience orbs.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> return@ModSetting cfg.isDisplayExperienceOrb },
        setValue = { cfg, value -> cfg.isDisplayExperienceOrb = value }
    ),
    ModSetting(
        title = "Message time",
        description = "Time in milliseconds to show message about pickup.",
        value = ModSettingValue.ValueInt(defaultValue = 1200, min = 400, max = 4000),
        getValue = { cfg -> return@ModSetting cfg.messageTime },
        setValue = { cfg, value -> cfg.messageTime = value }
    )
)

class ModMenuApiImpl : ModMenuApi {
    private fun <T> addAsEntry(
        entryBuilder: ConfigEntryBuilder?,
        configCategory: ConfigCategory?,
        setting: ModSetting<T>,
        modConfigCopy: ModConfig,
    ) {
        when(setting.value) {
            is ModSettingValue.ValueBoolean -> {
                setting as ModSetting<Boolean>
                configCategory?.addEntry(entryBuilder?.startBooleanToggle(Text.literal(setting.title), setting.getValue(modConfigCopy))
                    ?.setDefaultValue(setting.value.defaultValue)
                    ?.setTooltip(Text.literal(setting.description))
                    ?.setSaveConsumer({ newValue ->
                        setting.setValue(modConfigCopy, newValue)
                    })
                    ?.build())
            }

            is ModSettingValue.ValueInt -> {
                setting as ModSetting<Int>
                configCategory?.addEntry(entryBuilder?.startIntSlider(
                    Text.literal(setting.title),
                    setting.getValue(modConfigCopy),
                    setting.value.min,
                    setting.value.max
                )
                    ?.setDefaultValue(setting.value.defaultValue)
                    ?.setTooltip(Text.literal("..."))
                    ?.setSaveConsumer({ newValue ->
                        setting.setValue(modConfigCopy, newValue)
                    })
                    ?.build())
            }
        }
    }
    override fun getModConfigScreenFactory(): ConfigScreenFactory<Screen> {
        return ConfigScreenFactory { parent ->
            val modConfigCopy = ModConfig.INSTANCE.copy()
            val builder: ConfigBuilder? = ConfigBuilder.create()
                .setParentScreen(parent)
                .setSavingRunnable {
                    ModConfig.INSTANCE.apply(modConfigCopy)
                    ModConfig.save()
                }
                .setTitle(Text.literal("Pickup HUD Settings"))
            val entryBuilder = builder?.entryBuilder()

            val renderingCategory = builder?.getOrCreateCategory(Text.literal("Rendering"))
            renderingSettings.forEach { setting ->
                addAsEntry(entryBuilder, renderingCategory, setting, modConfigCopy)
            }

            val behaviorCategory = builder?.getOrCreateCategory(Text.literal("Behavior"))
            behaviorSettings.forEach { setting ->
                addAsEntry(entryBuilder, behaviorCategory, setting, modConfigCopy)
            }
            return@ConfigScreenFactory builder?.build()
        }
    }
}