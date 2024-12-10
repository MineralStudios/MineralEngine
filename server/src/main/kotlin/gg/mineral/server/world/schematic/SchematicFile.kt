package gg.mineral.server.world.schematic

import gg.mineral.server.world.chunk.ChunkImpl.Companion.toKey
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import java.io.File


class SchematicFile(val source: File, val xSize: Short, val ySize: Short, val zSize: Short) {
    val chunkedBlocks: Short2ObjectOpenHashMap<MutableList<SchematicBlock>> =
        object : Short2ObjectOpenHashMap<MutableList<SchematicBlock>>() {
            init {
                defaultReturnValue(emptyList<SchematicBlock>().toMutableList())
            }

            override fun get(key: Short): MutableList<SchematicBlock> = super.get(key)

            override fun getOrDefault(
                key: Short,
                defaultValue: MutableList<SchematicBlock>
            ): MutableList<SchematicBlock> =
                super.getOrDefault(key, defaultValue)

            override fun containsKey(key: Short): Boolean = super.containsKey(key)

            override fun remove(key: Short): MutableList<SchematicBlock> = super.remove(key)
        }

    fun getBlocksForChunk(chunkX: Byte, chunkZ: Byte): List<SchematicBlock> {
        val key = toKey(chunkX, chunkZ)
        return chunkedBlocks.remove(key)
    }

    val isEmpty: Boolean
        get() = chunkedBlocks.isEmpty()
}