package gg.mineral.api.entity;

import gg.mineral.api.MinecraftServer;
import gg.mineral.api.world.World;

public interface Entity {

    /**
     * Get the entity's world.
     * 
     * @return The entity's world.
     */
    World getWorld();

    /**
     * Get the entity's ID.
     * 
     * @return The entity's ID.
     */
    int getId();

    /**
     * Gets the entity's x coordinate.
     * 
     * @return The entity's x coordinate.
     */
    double getX();

    /**
     * Gets the entity's y coordinate.
     * 
     * @return The entity's y coordinate.
     */
    double getY();

    /**
     * Gets the entity's z coordinate.
     * 
     * @return The entity's z coordinate.
     */
    double getZ();

    /**
     * Sets the entity's x coordinate.
     * 
     * @param x The new x coordinate of the entity.
     */

    void setX(double x);

    /**
     * Sets the entity's y coordinate.
     * 
     * @param y The new y coordinate of the entity.
     */

    void setY(double y);

    /**
     * Sets the entity's z coordinate.
     * 
     * @param z The new z coordinate of the entity.
     */

    void setZ(double z);

    /**
     * Sets the entity's head y coordinate.
     * 
     * @param headY The new head y coordinate of the entity.
     */
    void setHeadY(double headY);

    /**
     * Gets the entity's pitch.
     * 
     * @return The entity's pitch.
     */
    float getPitch();

    /**
     * Gets the entity's yaw.
     * 
     * @return The entity's yaw.
     */
    float getYaw();

    /**
     * Sets the entity's pitch.
     * 
     * @param pitch The new pitch of the entity.
     */

    void setPitch(float pitch);

    /**
     * Sets the entity's yaw.
     * 
     * @param yaw The new yaw of the entity.
     */
    void setYaw(float yaw);

    /**
     * Set the X motion of the entity.
     * 
     * @param newMotX The new X motion of the entity.
     */
    void setMotX(double newMotX);

    /**
     * Set the Y motion of the entity.
     * 
     * @param newMotY The new Y motion of the entity.
     */
    void setMotY(double newMotY);

    /**
     * Set the Z motion of the entity.
     * 
     * @param newMotZ The new Z motion of the entity.
     */
    void setMotZ(double newMotZ);

    /**
     * Sets if the entity is on the ground.
     * 
     * @param onGround If the entity is on the ground.
     */
    void setOnGround(boolean onGround);

    /**
     * Sets if the entity needs a chunk update.
     * 
     * @param chunkUpdateNeeded If the entity needs a chunk update.
     */
    void setChunkUpdateNeeded(boolean chunkUpdateNeeded);

    /**
     * Gets the entity's current tick.
     * 
     * @return The entity's current tick.
     */
    int getCurrentTick();

    /**
     * Gets the entity's view distance.
     * 
     * @return The entity's view distance.
     */
    byte getViewDistance();

    /**
     * Attacks the target entity.
     * 
     * @param targetId The ID of the target entity.
     */
    void attack(int targetId);

    /**
     * Get the server.
     * 
     * @return The server.
     */
    MinecraftServer getServer();
}
