package gg.mineral.api.world

import gg.mineral.api.entity.Entity
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.world.chunk.Chunk
import kotlin.reflect.KClass

interface World {
    /**
     * Get the id of the world.
     *
     * @return The id of the world.
     */
    val id: Byte

    /**
     * Get the name of the world.
     *
     * @return The name of the world.
     */
    val name: String

    /**
     * Get the environment of the world.
     *
     * @return The environment of the world.
     */
    val environment: Environment

    /**
     * Get the generator of the world.
     *
     * @return The generator of the world.
     */
    val generator: Generator?

    /**
     * Get the chunk at the specified key.
     *
     * @param key The key of the chunk.
     * @return The chunk.
     */
    suspend fun getChunk(key: Short): Chunk?

    /**
     * Gets the entity with the specified ID.
     *
     * @param entityId The ID of the entity.
     *
     * @return The entity with the specified ID.
     */
    suspend fun getEntity(entityId: Int): Entity?

    /**
     * Gets the player with the specified entity ID.
     *
     * @param entityId The entity ID of the player.
     *
     * @return The player with the specified entity ID.
     */
    suspend fun getPlayer(entityId: Int): Player?

    /**
     * Get the type of the block at the specified coordinates.
     *
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param z The z-coordinate of the block.
     * @return The type of the block at the specified coordinates.
     */
    suspend fun getType(x: Int, y: Short, z: Int): Int

    /**
     * Get the metadata of the block at the specified coordinates.
     *
     * @param x The x-coordinate of the block.
     * @param y The y-coordinate of the block.
     * @param z The z-coordinate of the block.
     * @return The metadata of the block at the specified coordinates.
     */
    suspend fun getMetaData(x: Int, y: Short, z: Int): Int

    /**
     * Remove the entity with the specified ID.
     *
     * @param id The ID of the entity to remove.
     */
    suspend fun removeEntity(id: Int)

    /**
     * Add an entity to the world.
     *
     * @param entity The entity to add.
     */
    suspend fun addEntity(entity: Entity)

    /**
     * Get all the entities in the world.
     *
     * @return All the entities in the world.
     */
    suspend fun getEntities(): Collection<Entity>

    /**
     * Get the number of entities in the world.
     *
     * @return The number of entities in the world.
     */
    suspend fun <E : Entity> entityCount(kclass: KClass<E>): Int

    suspend fun broadcastMessage(message: String)


    fun interface Generator {
        /**
         * Generate a chunk.
         *
         * @param world
         * @param chunkX
         * @param chunkZ
         * @return The generated chunk.
         */
        fun generate(world: World, chunkX: Byte, chunkZ: Byte): Chunk
    }

    enum class Environment {
        NORMAL, NETHER, END
    }
}
