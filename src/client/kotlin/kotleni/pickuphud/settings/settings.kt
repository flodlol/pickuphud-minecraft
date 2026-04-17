package kotleni.pickuphud.settings

val renderingSettings = listOf(
    ModSetting(
        title = "Item icon",
        description = "Render item icon near to the message item.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> cfg.isRenderItemIcon },
        setValue = { cfg, value -> cfg.isRenderItemIcon = value }
    ),
    ModSetting(
        title = "Display rarity by text color",
        description = "Display item rarity by text color.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> cfg.isColorizeTextByRarity },
        setValue = { cfg, value -> cfg.isColorizeTextByRarity = value }
    ),
    ModSetting(
        title = "Total count in stacks",
        description = "Display total count in stacks + items if possible.",
        value = ModSettingValue.ValueBoolean(defaultValue = false),
        getValue = { cfg -> cfg.isDisplayTotalCountInStacks },
        setValue = { cfg, value -> cfg.isDisplayTotalCountInStacks = value }
    ),
    ModSetting(
        title = "Message padding",
        description = "Padding inside of message item.",
        value = ModSettingValue.ValueInt(defaultValue = 1, min = 0, max = 6),
        getValue = { cfg -> cfg.messagePadding },
        setValue = { cfg, value -> cfg.messagePadding = value }
    ),
    ModSetting(
        title = "Gap between messages",
        description = "Gap between messages",
        value = ModSettingValue.ValueInt(defaultValue = 2, min = 0, max = 6),
        getValue = { cfg -> cfg.gapBetweenMessages },
        setValue = { cfg, value -> cfg.gapBetweenMessages = value }
    ),
    ModSetting(
        title = "Align to right side",
        description = "When enabled, HUD is aligned to right edge. Otherwise left edge.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> cfg.isHudRightAligned },
        setValue = { cfg, value -> cfg.isHudRightAligned = value }
    ),
    ModSetting(
        title = "Align to bottom side",
        description = "When enabled, HUD grows from bottom. Otherwise from top.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> cfg.isHudBottomAligned },
        setValue = { cfg, value -> cfg.isHudBottomAligned = value }
    ),
    ModSetting(
        title = "Horizontal offset",
        description = "Distance from horizontal screen edge.",
        value = ModSettingValue.ValueInt(defaultValue = 4, min = 0, max = 400),
        getValue = { cfg -> cfg.hudOffsetX },
        setValue = { cfg, value -> cfg.hudOffsetX = value }
    ),
    ModSetting(
        title = "Vertical offset",
        description = "Distance from vertical screen edge.",
        value = ModSettingValue.ValueInt(defaultValue = 4, min = 0, max = 400),
        getValue = { cfg -> cfg.hudOffsetY },
        setValue = { cfg, value -> cfg.hudOffsetY = value }
    ),
    ModSetting(
        title = "Background shade",
        description = "Gray shade of message background (0=black, 255=white).",
        value = ModSettingValue.ValueInt(defaultValue = 32, min = 0, max = 255),
        getValue = { cfg -> cfg.messageBackgroundShade },
        setValue = { cfg, value -> cfg.messageBackgroundShade = value }
    ),
    ModSetting(
        title = "Background opacity",
        description = "Message background opacity (0 disables it).",
        value = ModSettingValue.ValueInt(defaultValue = 170, min = 0, max = 255),
        getValue = { cfg -> cfg.messageBackgroundOpacity },
        setValue = { cfg, value -> cfg.messageBackgroundOpacity = value }
    ),
    ModSetting(
        title = "Text background opacity",
        description = "Background behind text only (0 disables it).",
        value = ModSettingValue.ValueInt(defaultValue = 0, min = 0, max = 255),
        getValue = { cfg -> cfg.textBackgroundOpacity },
        setValue = { cfg, value -> cfg.textBackgroundOpacity = value }
    ),
)

val behaviorSettings = listOf(
    ModSetting(
        title = "Track experience orbs",
        description = "Track and display messages when pickup experience orbs.",
        value = ModSettingValue.ValueBoolean(defaultValue = true),
        getValue = { cfg -> cfg.isDisplayExperienceOrb },
        setValue = { cfg, value -> cfg.isDisplayExperienceOrb = value }
    ),
    ModSetting(
        title = "Message time",
        description = "Time in milliseconds to show message about pickup.",
        value = ModSettingValue.ValueInt(defaultValue = 1200, min = 400, max = 4000),
        getValue = { cfg -> cfg.messageTime },
        setValue = { cfg, value -> cfg.messageTime = value }
    ),
    ModSetting(
        title = "Messages on screen",
        description = "Count of max displayed messages on screen at the same time.",
        value = ModSettingValue.ValueInt(defaultValue = 12, min = 1, max = 32),
        getValue = { cfg -> cfg.maxMessagesOnScreen },
        setValue = { cfg, value -> cfg.maxMessagesOnScreen = value }
    ),
    ModSetting(
        title = "Only show tracked items",
        description = "Hide non-tracked item notifications from HUD.",
        value = ModSettingValue.ValueBoolean(defaultValue = false),
        getValue = { cfg -> cfg.isShowOnlyTrackedItems },
        setValue = { cfg, value -> cfg.isShowOnlyTrackedItems = value }
    ),
    ModSetting(
        title = "Play sound on tracked pickup",
        description = "Play pickup sound when tracked item is picked up.",
        value = ModSettingValue.ValueBoolean(defaultValue = false),
        getValue = { cfg -> cfg.isPlaySoundOnTrackedItem },
        setValue = { cfg, value -> cfg.isPlaySoundOnTrackedItem = value }
    ),
    ModSetting(
        title = "Pickup sound volume",
        description = "Volume for pickup sound, in percent.",
        value = ModSettingValue.ValueInt(defaultValue = 80, min = 0, max = 100),
        getValue = { cfg -> cfg.pickupSoundVolume },
        setValue = { cfg, value -> cfg.pickupSoundVolume = value }
    ),
)
