package gg.mineral.api.entity


import gg.mineral.api.snapshot.ServerSnapshot
import gg.mineral.api.world.World

interface Entity {
    /**
     * Get the entity's world.
     *
     * @return The entity's world.
     */
    val world: World

    /**
     * Get the entity's ID.
     *
     * @return The entity's ID.
     */
    val id: Int

    /**
     * Gets the entity's x coordinate.
     *
     * @return The entity's x coordinate.
     */
    /**
     * Sets the entity's x coordinate.
     *
     * @param x The new x coordinate of the entity.
     */
    var x: Double

    /**
     * Gets the entity's y coordinate.
     *
     * @return The entity's y coordinate.
     */
    /**
     * Sets the entity's y coordinate.
     *
     * @param y The new y coordinate of the entity.
     */
    var y: Double

    /**
     * Gets the entity's z coordinate.
     *
     * @return The entity's z coordinate.
     */
    /**
     * Sets the entity's z coordinate.
     *
     * @param z The new z coordinate of the entity.
     */
    var z: Double

    /**
     * Gets the entity's head y coordinate.
     *
     * @return The entity's head y coordinate.
     */
    /**
     * Sets the entity's head y coordinate.
     *
     * @param headY The new head y coordinate of the entity.
     */
    var headY: Double

    /**
     * Gets the entity's pitch.
     *
     * @return The entity's pitch.
     */
    /**
     * Sets the entity's pitch.
     *
     * @param pitch The new pitch of the entity.
     */
    var pitch: Float

    /**
     * Gets the entity's yaw.
     *
     * @return The entity's yaw.
     */
    /**
     * Sets the entity's yaw.
     *
     * @param yaw The new yaw of the entity.
     */
    var yaw: Float

    /**
     * Gets the x motion of the entity.
     *
     * @return The x motion of the entity.
     */
    /**
     * Set the X motion of the entity.
     *
     * @param newMotX The new X motion of the entity.
     */
    var motX: Double

    /**
     * Gets the Y motion of the entity.
     *
     * @return The Y motion of the entity.
     */
    /**
     * Set the Y motion of the entity.
     *
     * @param newMotY The new Y motion of the entity.
     */
    var motY: Double

    /**
     * Gets the Z motion of the entity.
     *
     * @return The Z motion of the entity.
     */
    /**
     * Set the Z motion of the entity.
     *
     * @param newMotZ The new Z motion of the entity.
     */
    var motZ: Double

    /**
     * Gets if the entity is on the ground.
     *
     * @return If the entity is on the ground.
     */
    /**
     * Sets if the entity is on the ground.
     *
     * @param onGround If the entity is on the ground.
     */
    var onGround: Boolean

    /**
     * Gets the entity's current tick.
     *
     * @return The entity's current tick.
     */
    val currentTick: Int

    /**
     * Gets the entity's current async tick.
     *
     * @return The entity's current async tick.
     */
    val currentAsyncTick: Int

    /**
     * Gets the entity's view distance.
     *
     * @return The entity's view distance.
     */
    var viewDistance: Byte

    /**
     * Get the entity's server snapshot.
     *
     * @return The entity's server snapshot.
     */
    val serverSnapshot: ServerSnapshot

    /**
     * Attacks the target entity.
     *
     * @param targetId The ID of the target entity.
     */
    suspend fun attack(targetId: Int)
}
