package kotleni.pickuphud

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import net.fabricmc.loader.api.FabricLoader
import java.io.File
import java.nio.charset.Charset
import kotlin.io.path.createDirectory
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.isWritable
import kotlin.io.path.readText
import kotlin.io.path.writeText

@Serializable
data class ModConfig(
    var isRenderItemIcon: Boolean = true,
    var isDisplayExperienceOrb: Boolean = true
) {
    fun apply(modConfig: ModConfig) {
        isRenderItemIcon = modConfig.isRenderItemIcon
        isDisplayExperienceOrb = modConfig.isDisplayExperienceOrb
    }

    companion object {
        private val modDir = FabricLoader.getInstance().gameDir.resolve("config")
        private val configPath = modDir.resolve("pickuphud-config.json")

        val INSTANCE = ModConfig()

        fun load() {
            if(configPath.exists()) {
                val content = configPath.readText(Charset.defaultCharset())
                val config = Json.decodeFromString<ModConfig>(content)
                INSTANCE.apply(config)
            }
        }

        fun save() {
            if(!modDir.exists()) modDir.createDirectory()

            if(!configPath.isDirectory()) {
                val json = Json.encodeToString(INSTANCE)
                configPath.writeText(json)
            } else {
                throw Exception("Can't write config file: ${configPath.toFile().path}")
            }
        }
    }
}