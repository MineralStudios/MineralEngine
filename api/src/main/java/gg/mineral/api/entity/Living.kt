package gg.mineral.api.entity

import gg.mineral.api.world.World

interface Living : Entity {
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
    fun teleport(world: World?, x: Double, y: Double, z: Double, yaw: Float, pitch: Float)

    /**
     * Teleport the entity to the specified location.
     *
     * @param world The world to teleport the entity to.
     * @param x     The x coordinate to teleport the entity to.
     * @param y     The y coordinate to teleport the entity to.
     * @param z     The z coordinate to teleport the entity to.
     */
    fun teleport(world: World?, x: Double, y: Double, z: Double)
}
