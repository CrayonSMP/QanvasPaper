package quove.qanvas.configs

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class QanvasConfig(
    @SerialName("config-version")
    override val configVersion: Int,
    @SerialName("world-settings")
    val worldSettings: Map<String, QanvasWorldConfig>
) : UpgradeableConfig {
    override fun isNewest() = true

    override fun upgradeToNext() = error("This config is already the newest version!")

    @Serializable
    class QanvasWorldConfig
}
