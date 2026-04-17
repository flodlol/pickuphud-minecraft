package kotleni.pickuphud

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import net.fabricmc.loader.api.FabricLoader
import java.nio.charset.Charset
import kotlin.io.path.createDirectory
import kotlin.io.path.copyTo
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.readText
import kotlin.io.path.writeText

@Serializable
data class ModConfig(
    var isRenderItemIcon: Boolean = true,
    var isDisplayExperienceOrb: Boolean = true,
    var messageTime: Int = 1200,
    var maxMessagesOnScreen: Int = 12,
    var isDisplayTotalCountInStacks: Boolean = false,
    var isColorizeTextByRarity: Boolean = true,

    var messagePadding: Int = 1,
    var gapBetweenMessages: Int = 2,

    var isHudRightAligned: Boolean = true,
    var isHudBottomAligned: Boolean = true,
    var hudOffsetX: Int = 4,
    var hudOffsetY: Int = 4,

    var trackedItemIdsCsv: String = "",
    var isShowOnlyTrackedItems: Boolean = false,
    var isPlaySoundOnTrackedItem: Boolean = false,
    var pickupSoundVolume: Int = 80,

    var messageBackgroundShade: Int = 32,
    var messageBackgroundOpacity: Int = 170,
    var textBackgroundOpacity: Int = 0,
) {
    fun apply(modConfig: ModConfig) {
        isRenderItemIcon = modConfig.isRenderItemIcon
        isDisplayExperienceOrb = modConfig.isDisplayExperienceOrb
        messageTime = modConfig.messageTime
        maxMessagesOnScreen = modConfig.maxMessagesOnScreen
        isDisplayTotalCountInStacks = modConfig.isDisplayTotalCountInStacks
        isColorizeTextByRarity = modConfig.isColorizeTextByRarity

        messagePadding = modConfig.messagePadding
        gapBetweenMessages = modConfig.gapBetweenMessages

        isHudRightAligned = modConfig.isHudRightAligned
        isHudBottomAligned = modConfig.isHudBottomAligned
        hudOffsetX = modConfig.hudOffsetX
        hudOffsetY = modConfig.hudOffsetY

        trackedItemIdsCsv = modConfig.trackedItemIdsCsv
        isShowOnlyTrackedItems = modConfig.isShowOnlyTrackedItems
        isPlaySoundOnTrackedItem = modConfig.isPlaySoundOnTrackedItem
        pickupSoundVolume = modConfig.pickupSoundVolume

        messageBackgroundShade = modConfig.messageBackgroundShade
        messageBackgroundOpacity = modConfig.messageBackgroundOpacity
        textBackgroundOpacity = modConfig.textBackgroundOpacity
    }

    companion object {
        private val modDir = FabricLoader.getInstance().gameDir.resolve("config")
        private val configPath = modDir.resolve("pickuphud-config.json")
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

        val INSTANCE = ModConfig()

        fun load() {
            if (configPath.exists()) {
                try {
                    val content = configPath.readText(Charset.defaultCharset())
                    val parsed = json.decodeFromString<JsonObject>(content)
                    val normalized = if (
                        "isPlaySoundOnTrackedItem" !in parsed &&
                        "isPlaySoundOnTrackedItemPickup" in parsed
                    ) {
                        JsonObject(parsed + ("isPlaySoundOnTrackedItem" to parsed.getValue("isPlaySoundOnTrackedItemPickup")))
                    } else parsed

                    val config = json.decodeFromJsonElement<ModConfig>(normalized)
                    INSTANCE.apply(config)
                } catch (_: Exception) {
                    runCatching {
                        if (!modDir.exists()) modDir.createDirectory()
                        configPath.copyTo(modDir.resolve("pickuphud-config.invalid.json"), overwrite = true)
                    }
                    save()
                }
            }
        }

        fun save() {
            if (!modDir.exists()) modDir.createDirectory()

            if (!configPath.isDirectory()) {
                configPath.writeText(json.encodeToString(INSTANCE))
            } else {
                throw Exception("Can't write config file: ${configPath.toFile().path}")
            }
        }
    }
}
