package kotleni.pickuphud.settings

sealed class ModSettingValue {
    data class ValueBoolean(
        val defaultValue: Boolean,
    ) : ModSettingValue()

    data class ValueInt(
        val defaultValue: Int,
        val min: Int,
        val max: Int,
    ) : ModSettingValue()

    data class ValueString(
        val defaultValue: String,
        val maxLength: Int,
    ) : ModSettingValue()
}
