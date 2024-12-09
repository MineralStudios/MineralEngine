package gg.mineral.api.entity;

import gg.mineral.api.world.World;

public interface Living extends Entity {
    /**
     * Teleport the entity to the specified location.
     * 
     * @param world The world to teleport the entity to.
     * @param x     The x coordinate to teleport the entity to.
     * @param y     The y coordinate to teleport the entity to.
     * @param z     The z coordinate to teleport the entity to.
     * @param yaw   The yaw to teleport the entity to.
     * @param pitch The pitch to teleport the entity to.
     */
    void teleport(World world, double x, double y, double z, float yaw, float pitch);

    /**
     * Teleport the entity to the specified location.
     * 
     * @param world The world to teleport the entity to.
     * @param x     The x coordinate to teleport the entity to.
     * @param y     The y coordinate to teleport the entity to.
     * @param z     The z coordinate to teleport the entity to.
     */
    void teleport(World world, double x, double y, double z);
}
