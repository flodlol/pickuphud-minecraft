package kotleni.pickuphud

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text
import kotlin.math.min

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

private val settings = listOf(
    ModSetting(
        title = "Item icon",
        description = "Render item icon near to the message item.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> return@ModSetting cfg.isRenderItemIcon },
        setValue = { cfg, value -> cfg.isRenderItemIcon = value }
    ),
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

            val general = builder?.getOrCreateCategory(Text.literal("General"))
            settings.forEach { setting ->

                when(setting.value) {
                    is ModSettingValue.ValueBoolean -> {
                        setting as ModSetting<Boolean>
                        general?.addEntry(entryBuilder?.startBooleanToggle(Text.literal(setting.title), setting.getValue(modConfigCopy))
                            ?.setDefaultValue(setting.value.defaultValue)
                            ?.setTooltip(Text.literal(setting.description))
                            ?.setSaveConsumer({ newValue ->
                                setting.setValue(modConfigCopy, newValue)
                            })
                            ?.build())
                }

                is ModSettingValue.ValueInt -> {
                    setting as ModSetting<Int>
                    general?.addEntry(entryBuilder?.startIntSlider(
                        Text.literal(setting.title),
                        setting.getValue(modConfigCopy),
                        setting.value.min,
                        setting.value.max
                    )
                        ?.setDefaultValue(setting.value.defaultValue)
//                        ?.setMin(setting.value.min)
//                        ?.setMax(setting.value.max)
                        ?.setTooltip(Text.literal("..."))
                        ?.setSaveConsumer({ newValue ->
                            setting.setValue(modConfigCopy, newValue)
                        })
                        ?.build())
                }
            }
            }
            return@ConfigScreenFactory builder?.build()
        }
    }
}