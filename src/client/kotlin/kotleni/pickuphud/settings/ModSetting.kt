package kotleni.pickuphud.settings

import kotleni.pickuphud.ModConfig

data class ModSetting<T>(
    val title: String,
    val description: String,
    val value: ModSettingValue,
    val getValue: (modConfig: ModConfig) -> T,
    val setValue: (modConfig: ModConfig, value: T) -> Unit
)