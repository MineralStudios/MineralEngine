package gg.mineral.api.entity.living;

import gg.mineral.api.entity.Living;
import gg.mineral.api.entity.living.human.property.Gamemode;

public interface Human extends Living {
    /**
     * Forces the player to swing their arm.
     */
    void swingArm();

    /**
     * Sets if the player is sprinting.
     * 
     * @param sprinting If the player is sprinting.
     */
    void setSprinting(boolean sprinting);

    /**
     * Sets if the player should deal extra knockback.
     * 
     * @param extraKnockback If the player should deal extra knockback.
     */
    void setExtraKnockback(boolean extraKnockback);

    /**
     * Gets the player's gamemode.
     * 
     * @return The player's gamemode.
     */
    Gamemode getGamemode();
}
