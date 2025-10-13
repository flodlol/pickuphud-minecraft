package kotleni.pickuphud

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.shedaniel.clothconfig2.api.ConfigBuilder
import net.minecraft.client.gui.screen.Screen
import net.minecraft.text.Text

sealed class ModSettingValue(
) {
    data class ValueBoolean(
        val defaultValue: Boolean,
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
    ModSetting<Boolean>(
        title = "Item icon",
        description = "Render item icon near to the message item.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> return@ModSetting cfg.isRenderItemIcon },
        setValue = { cfg, value -> cfg.isRenderItemIcon = value }
    ),
    ModSetting<Boolean>(
        title = "Track experience orbs",
        description = "Track and display messages when pickup experience orbs.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> return@ModSetting cfg.isDisplayExperienceOrb },
        setValue = { cfg, value -> cfg.isDisplayExperienceOrb = value }
    ),
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
                general!!.addEntry(
                    entryBuilder?.startBooleanToggle(Text.literal(setting.title), setting.getValue(modConfigCopy))
                        ?.setDefaultValue(true)
                        ?.setTooltip(Text.literal(setting.description))
                        ?.setSaveConsumer({ newValue ->
                            setting.setValue(modConfigCopy, newValue)
                        })
                        ?.build()
                )
            }
            return@ConfigScreenFactory builder?.build()
        }
    }
}