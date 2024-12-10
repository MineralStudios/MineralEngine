package gg.mineral.api.entity.living

import gg.mineral.api.entity.Living
import gg.mineral.api.entity.living.human.property.Gamemode

interface Human : Living {
    /**
     * Forces the player to swing their arm.
     */
    fun swingArm()

    /**
     * Sets if the player is sprinting.
     *
     * @param sprinting If the player is sprinting.
     */
    fun setSprinting(sprinting: Boolean)

    /**
     * Sets if the player should deal extra knockback.
     *
     * @param extraKnockback If the player should deal extra knockback.
     */
    fun setExtraKnockback(extraKnockback: Boolean)

    /**
     * Gets the player's gamemode.
     *
     * @return The player's gamemode.
     */
    val gamemode: Gamemode?
}
