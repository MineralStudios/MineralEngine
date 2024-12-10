package gg.mineral.server.world

import gg.mineral.api.entity.Entity
import gg.mineral.api.math.MathUtil
import gg.mineral.api.world.World
import gg.mineral.api.world.chunk.Chunk
import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.MinecraftServerImpl.entities
import gg.mineral.server.entity.EntityImpl
import gg.mineral.server.entity.living.human.PlayerImpl
import gg.mineral.server.network.packet.play.clientbound.MapChunkBulkPacket
import gg.mineral.server.world.chunk.ChunkImpl
import gg.mineral.server.world.chunk.ChunkImpl.Companion.toKey
import gg.mineral.server.world.chunk.ChunkImpl.Companion.xFromKey
import gg.mineral.server.world.chunk.ChunkImpl.Companion.zFromKey
import gg.mineral.server.world.chunk.ChunkImpl.entities
import gg.mineral.server.world.chunk.ChunkImpl.toPacket
import gg.mineral.server.world.chunk.EmptyChunkImpl
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import lombok.Getter

class WorldImpl(
    @field:Getter private val id: Byte,
    @field:Getter private val name: String,
    @field:Getter private val environment: World.Environment,
    @field:Getter private val generator: World.Generator?,
    @field:Getter val server: MinecraftServerImpl
) :
    World, MathUtil {
    private val chunkCache: Array<Chunk?> = arrayOfNulls<ChunkImpl>(65536)
    private val entities = IntOpenHashSet()
    private val entityChunkPositions: Int2ShortOpenHashMap = object : Int2ShortOpenHashMap() {
        init {
            defaultReturnValue(Short.MIN_VALUE)
        }
    }

    override fun getChunk(key: Short): Chunk {
        var chunk = chunkCache[unsigned(key)]
        val x = xFromKey(key)
        val z = zFromKey(key)

        if (chunk == null) {
            chunkCache[unsigned(key)] = if (generator != null)
                generator.generate(this, x, z)
            else
                ChunkImpl(this, x, z)
            chunk = chunkCache[unsigned(key)]
        }
        return chunk!!
    }

    fun setChunk(key: Short, chunk: ChunkImpl): Chunk {
        return chunk.also { chunkCache[unsigned(key)] = it }
    }

    override fun getType(x: Int, y: Short, z: Int): Int {
        return getChunk(toKey((x shr 4).toByte(), (z shr 4).toByte())).getType(x and 15, z and 15, y)
    }

    override fun getMetaData(x: Int, y: Short, z: Int): Int {
        return getChunk(toKey((x shr 4).toByte(), (z shr 4).toByte())).getMetaData(x and 15, z and 15, y).toInt()
    }

    override fun getEntity(entityId: Int): EntityImpl? {
        return if (entities.contains(entityId)) server.entities[entityId] else null
    }

    override fun getPlayer(entityId: Int): PlayerImpl? {
        return if (entities.contains(entityId)) server.players[entityId] else null
    }

    override fun removeEntity(id: Int) {
        entities.remove(id)
        val chunkKey = entityChunkPositions.remove(id)
        if (getChunk(chunkKey) is ChunkImpl) chunk.entities.remove(id)
    }

    override fun addEntity(entity: Entity) {
        val id = entity.id
        check(entities.add(id)) { "Entity with id $id already exists in world $name" }
        val chunkX = kotlin.math.floor(entity.x / 16).toInt().toByte()
        val chunkZ = kotlin.math.floor(entity.z / 16).toInt().toByte()
        val key = toKey(chunkX, chunkZ)
        entityChunkPositions.put(
            id,
            key
        )
        if (getChunk(key) is ChunkImpl) chunk.entities.add(id)
    }

    fun updatePosition(entity: Entity) {
        val id = entity.id
        val chunkX = kotlin.math.floor(entity.x / 16).toInt().toByte()
        val chunkZ = kotlin.math.floor(entity.z / 16).toInt().toByte()
        val newChunkKey = toKey(chunkX, chunkZ)
        val oldChunkKey = entityChunkPositions.put(
            id,
            newChunkKey
        )
        if (oldChunkKey != newChunkKey) {
            if (getChunk(oldChunkKey) is ChunkImpl) oldChunk.entities.remove(id)

            if (getChunk(newChunkKey) is ChunkImpl) newChunk.entities.add(id)

            if (entity is EntityImpl) entity.chunkUpdateNeeded = true
        }
    }

    fun getChunkLoadUpdates(player: PlayerImpl): MutableList<Chunk> {
        val viewDistance = player.viewDistance
        val chunkX = kotlin.math.floor(player.x / 16).toInt().toByte()
        val chunkZ = kotlin.math.floor(player.z / 16).toInt().toByte()
        val chunks = ArrayList<Chunk>()

        for (xOffset in -viewDistance..viewDistance) {
            val cX = (chunkX + xOffset).toByte()
            for (zOffset in -viewDistance..viewDistance) {
                val key = toKey(cX, (chunkZ + zOffset).toByte())
                player.chunkUpdateTracker.put(key, player.currentTick)
                if (!player.visibleChunks.contains(key)) {
                    player.visibleChunks.add(key)
                    val chunk = this.getChunk(key)
                    if (chunk != null) chunks.add(chunk)
                }
            }
        }
        return chunks
    }

    fun updateChunks(player: PlayerImpl) {
        player.chunkUpdateNeeded = false
        val chunks = getChunkLoadUpdates(player)

        val iterator = player.chunkUpdateTracker.short2IntEntrySet().fastIterator()

        val currentTick = player.currentTick

        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (currentTick - entry.intValue > 100) { // linked hashmap to order by eldest entry
                val key = entry.shortKey
                if (player.visibleChunks.remove(key)) {
                    chunks.add(EmptyChunkImpl(this, xFromKey(key), zFromKey(key)))

                    if (getChunk(key) is ChunkImpl) for (playerId in impl.entities) if (playerId != getId().toInt()) player.visibleEntities.remove(
                        playerId
                    )

                    iterator.remove()
                }
                continue
            }

            break
        }

        if (chunks.isEmpty()) return

        if (chunks.size == 1 && chunks.getFirst() is ChunkImpl) player.connection.queuePacket(impl.toPacket(true))
        else player.connection
            .queuePacket(MapChunkBulkPacket(environment == World.Environment.NORMAL, chunks))
    }

    companion object {
        const val MIN_CHUNK_COORD: Byte = -127
        const val MAX_CHUNK_COORD: Byte = 128.toByte()
    }
}
