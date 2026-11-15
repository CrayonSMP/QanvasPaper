package quove.qanvas.configs

import com.charleskorn.kaml.Yaml
import com.charleskorn.kaml.YamlConfiguration
import com.charleskorn.kaml.yamlMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import org.bukkit.Bukkit
import java.io.File
import java.util.logging.Level

object QanvasConfigUtils {
    private const val CURRENT_CONFIG_VERSION = 1

    val yaml = Yaml(
        configuration = YamlConfiguration(
            strictMode = false
        )
    )

    val deserializationStrategiesForVersions: Map<Int, KSerializer<out UpgradeableConfig>> = mapOf(
        CURRENT_CONFIG_VERSION to QanvasConfig.serializer()
    )

    lateinit var config: QanvasConfig

    fun init(configFile: File) {
        if (!configFile.exists()) {
            configFile.writeText(
                yaml.encodeToString(
                    QanvasConfig(
                        CURRENT_CONFIG_VERSION,
                        mapOf(
                            "default" to QanvasConfig.QanvasWorldConfig()
                        )
                    )
                )
            )
        }

        val loadedConfig = try {
            val yamlNode = yaml.parseToYamlNode(configFile.readText())
            val fileVersion = yamlNode.yamlMap.getScalar("config-version")?.toInt() ?: CURRENT_CONFIG_VERSION
            val strategy = deserializationStrategiesForVersions[fileVersion]
                ?: error("Unknown qanvas.yml config version $fileVersion")

            var current: UpgradeableConfig = yaml.decodeFromYamlNode(strategy, yamlNode)
            while (!current.isNewest()) {
                Bukkit.getLogger().log(Level.INFO, "Attempting to upgrade Qanvas Config from version ${current.configVersion} to the next version...")
                val next = current.upgradeToNext()
                Bukkit.getLogger().log(Level.INFO, "Upgraded Qanvas Config from version ${current.configVersion} to ${next.configVersion}")
                current = next
            }
            current as QanvasConfig
        } catch (e: SerializationException) {
            Bukkit.getLogger().log(Level.SEVERE, "Could not load qanvas.yml, please correct your syntax errors", e)
            throw IllegalStateException("Could not load qanvas.yml", e)
        }

        configFile.writeText(yaml.encodeToString(loadedConfig))
        config = loadedConfig
    }

    fun getWorldSettings(levelName: String): QanvasConfig.QanvasWorldConfig {
        return config.worldSettings[levelName] ?: config.worldSettings["default"] ?: error("Missing default world-settings in qanvas.yml!")
    }
}
