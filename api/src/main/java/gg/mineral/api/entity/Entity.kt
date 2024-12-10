package gg.mineral.api.entity

import gg.mineral.api.MinecraftServer
import gg.mineral.api.world.World

interface Entity {
    /**
     * Get the entity's world.
     *
     * @return The entity's world.
     */
    val world: World?

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
     * Sets the entity's head y coordinate.
     *
     * @param headY The new head y coordinate of the entity.
     */
    fun setHeadY(headY: Double)

    /**
     * Gets the entity's pitch.
     *
     * @return The entity's pitch.
     */
    fun getPitch(): Float

    /**
     * Gets the entity's yaw.
     *
     * @return The entity's yaw.
     */
    fun getYaw(): Float

    /**
     * Sets the entity's pitch.
     *
     * @param pitch The new pitch of the entity.
     */
    fun setPitch(pitch: Float)

    /**
     * Sets the entity's yaw.
     *
     * @param yaw The new yaw of the entity.
     */
    fun setYaw(yaw: Float)

    /**
     * Set the X motion of the entity.
     *
     * @param newMotX The new X motion of the entity.
     */
    fun setMotX(newMotX: Double)

    /**
     * Set the Y motion of the entity.
     *
     * @param newMotY The new Y motion of the entity.
     */
    fun setMotY(newMotY: Double)

    /**
     * Set the Z motion of the entity.
     *
     * @param newMotZ The new Z motion of the entity.
     */
    fun setMotZ(newMotZ: Double)

    /**
     * Sets if the entity is on the ground.
     *
     * @param onGround If the entity is on the ground.
     */
    fun setOnGround(onGround: Boolean)

    /**
     * Sets if the entity needs a chunk update.
     *
     * @param chunkUpdateNeeded If the entity needs a chunk update.
     */
    fun setChunkUpdateNeeded(chunkUpdateNeeded: Boolean)

    /**
     * Gets the entity's current tick.
     *
     * @return The entity's current tick.
     */
    val currentTick: Int

    /**
     * Gets the entity's view distance.
     *
     * @return The entity's view distance.
     */
    val viewDistance: Byte

    /**
     * Attacks the target entity.
     *
     * @param targetId The ID of the target entity.
     */
    fun attack(targetId: Int)

    /**
     * Get the server.
     *
     * @return The server.
     */
    val server: MinecraftServer?
}
