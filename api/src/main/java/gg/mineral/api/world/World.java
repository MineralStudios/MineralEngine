package gg.mineral.api.world;

import gg.mineral.api.entity.Entity;
import gg.mineral.api.entity.living.human.Player;
import gg.mineral.api.world.chunk.Chunk;

public interface World {

    /**
     * Get the id of the world.
     * 
     * @return The id of the world.
     */
    byte getId();

    /**
     * Get the name of the world.
     * 
     * @return The name of the world.
     */
    String getName();

    /**
     * Get the environment of the world.
     * 
     * @return The environment of the world.
     */
    Environment getEnvironment();

    /**
     * Get the generator of the world.
     * 
     * @return The generator of the world.
     */
    Generator getGenerator();

    /**
     * Get the chunk at the specified key.
     * 
     * @param key The key of the chunk.
     * @return The chunk.
     */
    Chunk getChunk(short key);

    /**
     * Gets the entity with the specified ID.
     * 
     * @param entityId The ID of the entity.
     * 
     * @return The entity with the specified ID.
     */
    Entity getEntity(int entityId);

    /**
     * Gets the player with the specified entity ID.
     * 
     * @param entityId The entity ID of the player.
     * 
     * @return The player with the specified entity ID.
     */
    Player getPlayer(int entityId);

    /**
     * Get the type of the block at the specified coordinates.
     * 
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param z The z-coordinate of the block.
     * @return The type of the block at the specified coordinates.
     */
    int getType(int x, short y, int z);

    /**
     * Get the metadata of the block at the specified coordinates.
     * 
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param z The z-coordinate of the block.
     * @return The metadata of the block at the specified coordinates.
     */
    int getMetaData(int x, short y, int z);

    /**
     * Remove the entity with the specified ID.
     * 
     * @param id The ID of the entity to remove.
     */
    void removeEntity(int id);

    /**
     * Add an entity to the world.
     * 
     * @param entity The entity to add.
     */
    void addEntity(Entity entity);

    public static interface Generator {
        /**
         * Generate a chunk.
         * 
         * @param world
         * @param chunkX
         * @param chunkZ
         * @return The generated chunk.
         */
        Chunk generate(World world, byte chunkX, byte chunkZ);
    }

    public static enum Environment {
        NORMAL, NETHER, END
    }
}
