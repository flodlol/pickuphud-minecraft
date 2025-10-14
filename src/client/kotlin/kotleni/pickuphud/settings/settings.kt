package kotleni.pickuphud.settings

val renderingSettings = listOf(
    ModSetting(
        title = "Item icon",
        description = "Render item icon near to the message item.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> return@ModSetting cfg.isRenderItemIcon },
        setValue = { cfg, value -> cfg.isRenderItemIcon = value }
    ),
    ModSetting(
        title = "Display rarity by text color",
        description = "Display item rarity by text color.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> return@ModSetting cfg.isColorizeTextByRarity },
        setValue = { cfg, value -> cfg.isColorizeTextByRarity = value }
    ),
    ModSetting(
        title = "Total count in stacks",
        description = "Display total count in stacks + items if possible.",
        value = ModSettingValue.ValueBoolean(defaultValue = false),
        getValue = { cfg -> return@ModSetting cfg.isDisplayTotalCountInStacks },
        setValue = { cfg, value -> cfg.isDisplayTotalCountInStacks = value }
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
    ),
    ModSetting(
        title = "Messages on screen",
        description = "Count of max displayed messages on screen at the same time.",
        value = ModSettingValue.ValueInt(defaultValue = 12, min = 1, max = 32),
        getValue = { cfg -> return@ModSetting cfg.maxMessagesOnScreen },
        setValue = { cfg, value -> cfg.maxMessagesOnScreen = value }
    ),
)