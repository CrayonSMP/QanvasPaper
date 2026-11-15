package quove.qanvas.configs

interface UpgradeableConfig {
    val configVersion: Int
    fun isNewest(): Boolean
    fun upgradeToNext(): UpgradeableConfig
}
