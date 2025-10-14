package kotleni.pickuphud.settings

val renderingSettings = listOf(
    ModSetting(
        title = "Item icon",
        description = "Render item icon near to the message item.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> return@ModSetting cfg.isRenderItemIcon },
        setValue = { cfg, value -> cfg.isRenderItemIcon = value }
    ),
)

val behaviorSettings = listOf(
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