package kotleni.pickuphud

import kotleni.pickupnotif.client.PickupMessage

data class ModConfig(
    var isRenderItemIcon: Boolean = true,
    var isDisplayExperienceOrb: Boolean = true
) {
    fun apply(modConfig: ModConfig) {
        isRenderItemIcon = modConfig.isRenderItemIcon
        isDisplayExperienceOrb = modConfig.isDisplayExperienceOrb
    }

    companion object {
        val INSTANCE = ModConfig()
    }
}