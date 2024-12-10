package gg.mineral.server.world.schematic

import gg.mineral.server.world.chunk.ChunkImpl.Companion.toKey
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.Setter
import java.io.File

@Getter
@AllArgsConstructor
class SchematicFile {
    val chunkedBlocks: Short2ObjectOpenHashMap<List<SchematicBlock>> =
        object : Short2ObjectOpenHashMap<List<SchematicBlock?>?>() {
            init {
                defaultReturnValue(emptyList<SchematicBlock>())
            }
        }
    private val source: File? = null

    @Setter
    private val xSize: Short = 0

    @Setter
    private val ySize: Short = 0

    @Setter
    private val zSize: Short = 0

    fun getBlocksForChunk(chunkX: Byte, chunkZ: Byte): List<SchematicBlock> {
        val key = toKey(chunkX, chunkZ)
        return chunkedBlocks.remove(key)
    }

    val isEmpty: Boolean
        get() = chunkedBlocks.isEmpty()
}