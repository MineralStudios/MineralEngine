package gg.mineral.server.world

import gg.mineral.api.entity.Entity
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.math.MathUtil.floor
import gg.mineral.api.math.MathUtil.unsigned
import gg.mineral.api.world.World
import gg.mineral.api.world.chunk.Chunk
import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.entity.EntityImpl
import gg.mineral.server.entity.living.human.PlayerImpl
import gg.mineral.server.world.chunk.ChunkImpl
import gg.mineral.server.world.chunk.ChunkImpl.Companion.toKey
import gg.mineral.server.world.chunk.ChunkImpl.Companion.xFromKey
import gg.mineral.server.world.chunk.ChunkImpl.Companion.zFromKey
import gg.mineral.server.world.chunk.EmptyChunkImpl
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import java.util.concurrent.atomic.AtomicReferenceArray
import kotlin.reflect.KClass

class WorldImpl(
    override val id: Byte,
    override val name: String,
    override val environment: World.Environment,
    override val generator: World.Generator?,
    val server: MinecraftServerImpl
) :
    World {
    private val chunkPositionsMutex = Mutex()
    private val entitiesMutex = Mutex()

    private val chunkCache: AtomicReferenceArray<ChunkImpl?> by lazy { AtomicReferenceArray(65536) }
    private val entitiesMap by lazy { Int2ObjectOpenHashMap<EntityImpl?>() }
    private val entityChunkPositions by lazy {
        object : Int2ShortOpenHashMap() {
            init {
                defaultReturnValue(Short.MIN_VALUE)
            }

            override fun get(key: Int): Short = super.get(key)

            override fun getOrDefault(key: Int, defaultValue: Short): Short = super.getOrDefault(key, defaultValue)

            override fun containsKey(key: Int): Boolean = super.containsKey(key)

            override fun remove(k: Int): Short = super.remove(k)
        }
    }

    override fun getChunk(key: Short): Chunk {
        val unsignedKey = unsigned(key)
        return chunkCache[unsignedKey] ?: run {
            val x = xFromKey(key)
            val z = zFromKey(key)
            val newChunk =
                generator?.generate(this@WorldImpl, x, z) ?: EmptyChunkImpl(
                    this@WorldImpl,
                    x,
                    z
                )
            chunkCache[unsignedKey] = newChunk as ChunkImpl
            newChunk
        }
    }

    override suspend fun getType(x: Int, y: Short, z: Int): Int {
        return getChunk(toKey((x shr 4).toByte(), (z shr 4).toByte())).getType(x and 15, z and 15, y)
    }

    override suspend fun getMetaData(x: Int, y: Short, z: Int): Int {
        return getChunk(toKey((x shr 4).toByte(), (z shr 4).toByte())).getMetaData(x and 15, z and 15, y).toInt()
    }

    override suspend fun getEntity(entityId: Int): EntityImpl? {
        return entitiesMutex.withLock { entitiesMap.get(entityId) }
    }

    override suspend fun getPlayer(entityId: Int): PlayerImpl? {
        return getEntity(entityId) as? PlayerImpl
    }

    override suspend fun removeEntity(id: Int) {
        entitiesMutex.withLock {
            entitiesMap.remove(id)
        }
        val chunkKey = chunkPositionsMutex.withLock { entityChunkPositions.remove(id) }
        val chunk = getChunk(chunkKey)
        if (chunk is ChunkImpl) chunk.removeEntity(id)
    }

    override suspend fun addEntity(entity: Entity) {
        val id = entity.id
        check(entity is EntityImpl) { "Entity must be an instance of EntityImpl" }

        entitiesMutex.withLock {
            if (entitiesMap.put(id, entity) != null) return
        }

        val chunkX = floor(entity.x / 16).toByte()
        val chunkZ = floor(entity.z / 16).toByte()
        val key = toKey(chunkX, chunkZ)

        chunkPositionsMutex.withLock {
            entityChunkPositions.put(
                id,
                key
            )
        }

        val chunk = getChunk(key)
        if (chunk is ChunkImpl) chunk.addEntity(id)
    }

    override suspend fun getEntities(): Collection<Entity> {
        return entitiesMutex.withLock { Collections.unmodifiableCollection(entitiesMap.values.filterNotNull()) }
    }

    override suspend fun <E : Entity> entityCount(kclass: KClass<E>): Int {
        return entitiesMutex.withLock { entitiesMap.int2ObjectEntrySet().filter { kclass.isInstance(it) }.size }
    }

    override suspend fun broadcastMessage(message: String) {
        entitiesMutex.withLock {
            entitiesMap.values.filterIsInstance<Player>().forEach { it.msg(message) }
        }
    }

    fun updateEntityChunks(newChunkKey: Short, oldChunkKey: Short, entity: Entity) {
        if (oldChunkKey != newChunkKey && entity is EntityImpl) entity.chunkUpdateNeeded = true
    }

    suspend fun updatePosition(entity: Entity) {
        val id = entity.id
        val chunkX = floor(entity.x / 16).toByte()
        val chunkZ = floor(entity.z / 16).toByte()
        val newChunkKey = toKey(chunkX, chunkZ)

        val oldChunkKey = chunkPositionsMutex.withLock {
            entityChunkPositions.put(
                id,
                newChunkKey
            )
        }

        if (oldChunkKey != newChunkKey) {
            val oldChunk = getChunk(oldChunkKey)
            val newChunk = getChunk(newChunkKey)
            if (oldChunk is ChunkImpl) oldChunk.removeEntity(id)
            if (newChunk is ChunkImpl) newChunk.addEntity(id)
        }
    }
}
