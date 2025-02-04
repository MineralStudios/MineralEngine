package gg.mineral.api.entity.living

import gg.mineral.api.entity.Living
import gg.mineral.api.entity.living.human.property.Gamemode

interface Human : Living {
    /**
     * Forces the player to swing their arm.
     */
    suspend fun swingArm()

    /**
     * Gets if the player is sprinting.
     *
     * @return If the player is sprinting.
     */
    /**
     * Sets if the player is sprinting.
     *
     * @param sprinting If the player is sprinting.
     */
    var sprinting: Boolean

    /**
     * Gets if the player should deal extra knockback.
     *
     * @return If the player should deal extra knockback.
     */
    /**
     * Sets if the player should deal extra knockback.
     *
     * @param extraKnockback If the player should deal extra knockback.
     */
    var extraKnockback: Boolean

    /**
     * Gets the player's gamemode.
     *
     * @return The player's gamemode.
     */
    var gamemode: Gamemode

    /**
     * Gets the player's name
     *
     * @return The player's name
     */
    val name: String
}
