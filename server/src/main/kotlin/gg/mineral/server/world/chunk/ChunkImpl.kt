package gg.mineral.server.world.chunk

import gg.mineral.api.world.World
import gg.mineral.api.world.chunk.Chunk
import gg.mineral.server.network.packet.play.clientbound.ChunkDataPacket
import gg.mineral.server.util.collection.NibbleArray
import gg.mineral.server.world.block.Block
import it.unimi.dsi.fastutil.ints.IntOpenHashSet
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.atomic.AtomicReferenceArray
import java.util.zip.DeflaterOutputStream

open class ChunkImpl(
    private val world: World,
    override val x: Byte, override val z: Byte
) : Chunk {
    private val entitiesMutex = Mutex()
    private val entities by lazy { IntOpenHashSet() }
    private val cache by lazy { arrayOfNulls<ChunkDataPacket>(4) }

    suspend fun removeEntity(entityId: Int) {
        entitiesMutex.withLock { entities.remove(entityId) }
    }

    suspend fun addEntity(entityId: Int) {
        entitiesMutex.withLock { entities.add(entityId) }
    }

    suspend fun entityIterator(): it.unimi.dsi.fastutil.ints.IntIterator {
        return entitiesMutex.withLock { entities.iterator() }
    }

    /**
     * The array of chunk sections this chunk contains, or null if it is unloaded.
     */
    private val sections by lazy { AtomicReferenceArray<ChunkSection>(DEPTH / SEC_DEPTH) }

    /**
     * The array of biomes this chunk contains, or null if it is unloaded.
     */
    private var biomes: ByteArray? = null

    private fun resetCache() {
        for (i in cache.indices) cache[i] = null
    }

    private fun getCache(skylight: Boolean, compress: Boolean): ChunkDataPacket? {
        val index = if (skylight && compress) 3
        else if (skylight) 2
        else if (compress) 1
        else 0
        return cache[index]
    }

    private fun setCache(skylight: Boolean, compress: Boolean, packet: ChunkDataPacket?) {
        val index = if (skylight && compress) 3
        else if (skylight) 2
        else if (compress) 1
        else 0
        cache[index] = packet
    }

    // ======== Basic stuff ========
    fun getBlock(x: Int, y: Short, z: Int): Block {
        return Block(
            (this.x.toInt() shl 4) or (x and 0xf), y.toInt() and 0xff, (this.z.toInt() shl 4) or
                    (z and 0xf), getType(x, z, y), getMetaData(x, z, y)
        )
    }

    // ======== Data access ========
    /**
     * Attempt to get the ChunkSection at the specified height.
     *
     * @param y the y value.
     * @return The ChunkSection, or null if it is empty.
     */
    private fun getSection(y: Short): ChunkSection? {
        val idx = y.toInt() shr 4
        if (y < 0 || y >= DEPTH || idx >= sections.length()) return null

        return sections[idx]
    }

    /**
     * Gets the type of a block within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The type.
     */
    override fun getType(x: Int, z: Int, y: Short): Int {
        val section = getSection(y)
        return if (section == null) 0 else (section.types[section.index(x, y, z)].toInt() and 0xff)
    }

    /**
     * Sets the type of a block within this chunk.
     *
     * @param x    The X coordinate.
     * @param z    The Z coordinate.
     * @param y    The Y coordinate.
     * @param type The type.
     */
    fun setType(x: Int, z: Int, y: Short, type: Int) {
        if (type < 0 || type > 0xfff) return

        resetCache()

        var section = getSection(y)
        if (section == null) {
            if (type == 0) {
                // don't need to create chunk for air
                return
            } else {
                // create new ChunkSection for this y coordinate
                val idx = y.toInt() shr 4
                if (y < 0 || y >= DEPTH || idx >= sections.length()) {
                    // y is out of range somehow
                    return
                }
                section = ChunkSection()
                sections[idx] = section
            }
        }

        // update the air count and height map
        val index = section.index(x, y, z)
        if (type == 0) {
            if (section.types[index].toInt() != 0) section.count--
        } else {
            if (section.types[index].toInt() == 0) section.count++
        }
        // update the type - also sets metadata to 0
        section.types[section.index(x, y, z)] = type.toByte()

        if (type == 0 && section.count == 0) {
            // destroy the empty section
            sections[y / SEC_DEPTH] = null
            return
        }
    }

    override fun getMetaData(x: Int, z: Int, y: Short): Byte {
        val section = getSection(y)
        return section?.metaData?.get(section.index(x, y, z)) ?: 0
    }

    fun setMetaData(x: Int, z: Int, y: Short, metaData: Byte) {
        require(!(metaData < 0 || metaData >= 16)) { "Metadata out of range: $metaData" }

        resetCache()
        val section = getSection(y) ?: return
        // can't set metadata on an empty section

        section.metaData[section.index(x, y, z)] = metaData
    }

    fun getSkyLight(x: Int, z: Int, y: Short): Byte {
        val section = getSection(y)
        return section?.skyLight?.get(section.index(x, y, z)) ?: 0
    }

    fun setSkyLight(x: Int, z: Int, y: Short, skyLight: Int) {
        val section = getSection(y) ?: return
        // can't set light on an empty section

        resetCache()
        section.skyLight[section.index(x, y, z)] = skyLight.toByte()
    }

    fun getBlockLight(x: Int, z: Int, y: Short): Byte {
        val section = getSection(y)
        return section?.blockLight?.get(section.index(x, y, z)) ?: 0
    }

    fun setBlockLight(x: Int, z: Int, y: Short, blockLight: Int) {
        val section = getSection(y) ?: return
        // can't set light on an empty section

        resetCache()
        section.blockLight[section.index(x, y, z)] = blockLight.toByte()
    }

    /**
     * Gets the biome of a column within this chunk.
     *
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return The biome.
     */
    fun getBiome(x: Int, z: Int): Int {
        if (biomes == null) return 0
        return biomes!![z * WIDTH + x].toInt() and 0xFF
    }

    /**
     * Sets the biome of a column within this chunk,
     *
     * @param x     The X coordinate.
     * @param z     The Z coordinate.
     * @param biome The biome.
     */
    fun setBiome(x: Int, z: Int, biome: Int) {
        if (biomes == null) return
        resetCache()
        biomes!![z * WIDTH + x] = biome.toByte()
    }

    /**
     * Set the entire biome array of this chunk.
     *
     * @param newBiomes The biome array.
     */
    fun setBiomes(newBiomes: ByteArray) {
        checkNotNull(biomes) { "Must initialize chunk first" }

        require(newBiomes.size == biomes!!.size) { "Biomes array not of length " + biomes!!.size }
        resetCache()

        biomes?.let { System.arraycopy(newBiomes, 0, it, 0, biomes!!.size) }
    }

    fun toPacket(compress: Boolean): ChunkDataPacket {
        return toPacket(world.environment == World.Environment.NORMAL, compress)
    }

    open fun toPacket(skylight: Boolean, compress: Boolean): ChunkDataPacket {
        val cache = getCache(skylight, compress)
        if (cache != null) return cache

        val entireChunk = true // TODO: don't send entire chunk if not needed

        var primaryBitmap = 0
        val addBitmap = 0
        var numSections = 0

        for (i in 0..15) {
            val section = sections[i]
            if (section == null || section.isEmpty) continue

            primaryBitmap = primaryBitmap or (1 shl i)
            numSections++
        }

        val sizeBlockTypes = numSections * 4096
        val sizeMetadata = numSections * 2048
        val sizeBlockLight = numSections * 2048
        val sizeSkyLight = if (skylight) numSections * 2048 else 0
        val sizeBiomes = if (entireChunk) 256 else 0

        val totalSize = sizeBlockTypes + sizeMetadata + sizeBlockLight + sizeSkyLight + sizeBiomes

        val output = ByteArray(totalSize)

        val posBlockTypes = 0
        val posMetadata = posBlockTypes + sizeBlockTypes
        val posBlockLight = posMetadata + sizeMetadata
        val posSkyLight = posBlockLight + sizeBlockLight
        val posAddData = posSkyLight + sizeSkyLight

        var sectionIndex = 0
        for (i in 0..15) {
            if ((primaryBitmap and (1 shl i)) == 0) continue

            var section = sections[i]
            if (section == null) section = EMPTY_SECTION

            val offset = posBlockTypes + sectionIndex * 4096
            System.arraycopy(section.types, 0, output, offset, 4096)

            sectionIndex++
        }

        sectionIndex = 0
        for (i in 0..15) {
            if ((primaryBitmap and (1 shl i)) == 0) continue

            var section = sections[i]
            if (section == null) section = EMPTY_SECTION

            val offset = posMetadata + sectionIndex * 2048
            System.arraycopy(section.metaData.rawData, 0, output, offset, 2048)

            sectionIndex++
        }

        sectionIndex = 0
        for (i in 0..15) {
            if ((primaryBitmap and (1 shl i)) == 0) continue

            var section = sections[i]
            if (section == null) section = EMPTY_SECTION

            val offset = posBlockLight + sectionIndex * 2048
            System.arraycopy(section.blockLight.rawData, 0, output, offset, 2048)

            sectionIndex++
        }

        if (skylight) {
            sectionIndex = 0
            for (i in 0..15) {
                if ((primaryBitmap and (1 shl i)) == 0) continue

                var section = sections[i]
                if (section == null) section = EMPTY_SECTION

                val offset = posSkyLight + sectionIndex * 2048
                System.arraycopy(section.skyLight.rawData, 0, output, offset, 2048)

                sectionIndex++
            }
        }

        if (entireChunk)
            System.arraycopy(this.biomes ?: ByteArray(256), 0, output, posAddData, 256)

        val dataToSend = if (compress) compress(output, totalSize) else output
        val packet = ChunkDataPacket(x.toInt(), z.toInt(), entireChunk, primaryBitmap, addBitmap, dataToSend)
        setCache(skylight, compress, packet)

        return packet
    }

    class ChunkSection {
        val types = ByteArray(ARRAY_SIZE)
        val metaData = NibbleArray(ARRAY_SIZE)
        val skyLight = NibbleArray(ARRAY_SIZE, 0xf.toByte())
        val blockLight = NibbleArray(ARRAY_SIZE)
        var count = 0

        /**
         * Create a new, empty ChunkSection.
         */
        init {
            recount()
        }

        private fun recount() {
            count = 0
            for (type in types) if (type.toInt() != 0) count++
        }

        fun index(x: Int, y: Short, z: Int): Int {
            if (x < 0 || z < 0 || x >= WIDTH || z >= HEIGHT) throw IndexOutOfBoundsException(
                "Coords (x=$x,z=$z) out of section bounds"
            )

            return ((y.toInt() and 0xf) shl 8) or (z shl 4) or x
        }

        val isEmpty: Boolean
            get() {
                for (type in types) if (type.toInt() != 0) return false

                return true
            }
    }

    companion object {
        /**
         * The dimensions of a chunk (width: x, height: z, depth: y).
         */
        const val WIDTH = 16
        const val HEIGHT = 16
        const val DEPTH = 256
        private val EMPTY_SECTION by lazy { ChunkSection() }

        /**
         * The Y depth of a single chunk section.
         */
        private const val SEC_DEPTH = 16
        private const val ARRAY_SIZE = WIDTH * HEIGHT * SEC_DEPTH

        fun toKey(x: Byte, z: Byte) = toKey(x.toInt(), z.toInt())

        fun toKey(x: Int, z: Int) = ((x shl 8) or (z and 0xFF)).toShort()

        fun xFromKey(key: Short) = ((key.toInt() shr 8) and 0xFF).toByte()

        fun zFromKey(key: Short) = (key.toInt() and 0xFF).toByte()

        @JvmStatic
        fun compress(data: ByteArray, length: Int): ByteArray {
            val outputStream = ByteArrayOutputStream()
            try {
                DeflaterOutputStream(outputStream).use { deflaterOutputStream ->
                    deflaterOutputStream.write(data, 0, length)
                }
            } catch (e: IOException) {
                throw RuntimeException("Failed to compress chunk data", e)
            }
            return outputStream.toByteArray()
        }
    }
}