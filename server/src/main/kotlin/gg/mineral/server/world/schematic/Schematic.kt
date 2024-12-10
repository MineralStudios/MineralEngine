package gg.mineral.server.world.schematic

import gg.mineral.api.nbt.*
import gg.mineral.server.world.chunk.ChunkImpl.Companion.toKey
import it.unimi.dsi.fastutil.shorts.Short2ObjectFunction
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

object Schematic {
    /**
     * Loads a schematic from a source file.
     *
     * @param source the source file
     * @return the loaded schematic
     */
    fun load(source: File): SchematicFile {
        val stream = NBTInputStream(FileInputStream(source))
        val nbt = stream.readCompound()

        val nbtValue = nbt.value

        val widthTag = nbtValue["Width"]
        val heightTag = nbtValue["Height"]
        val lengthTag = nbtValue["Length"]

        require(!(widthTag == null || heightTag == null || lengthTag == null)) { "Invalid schematic file: missing dimensions" }

        if (widthTag.value is Short && heightTag.value is Short
            && lengthTag.value is Short
        ) {
            val width = widthTag.value as Short
            val height = heightTag.value as Short
            val length = lengthTag.value as Short
            val schematic = SchematicFile(source, width, height, length)

            val blocksTag = nbtValue["Blocks"]
            val dataTag = nbtValue["Data"]

            require(!(blocksTag == null || dataTag == null)) { "Invalid schematic file: missing blocks" }

            if (blocksTag.value is ByteArray && dataTag.value is ByteArray) {
                val chunkedBlocks = schematic.chunkedBlocks
                val blockArray = blocksTag.value as ByteArray
                val dataArray = dataTag.value as ByteArray

                for (x in 0..<width) {
                    val chunkX = (x shr 4).toByte()

                    for (y in 0..<height) {
                        for (z in 0..<length) {
                            val chunkZ = (z shr 4).toByte()

                            val index: Int = (y * length + z) * width + x
                            val type: Int = blockArray[index].toInt() and 0xFF
                            val data: Byte = (dataArray[index].toInt() and 0xF).toByte()

                            val block = SchematicBlock(x, y, z, type, data)

                            val chunkKey = toKey(chunkX, chunkZ)

                            chunkedBlocks.computeIfAbsent(
                                chunkKey,
                                Short2ObjectFunction<MutableList<SchematicBlock>> { ArrayList() })
                                .add(block)
                        }
                    }
                }

                return schematic
            }
        }

        throw IllegalArgumentException("Invalid schematic file: invalid dimensions")
    }

    /**
     * Saves a schematic file to the given destination
     *
     * @param schematic the schematic to save
     * @throws IOException if the schematic could not be saved
     */
    /**
     * Saves a schematic file to its source file
     *
     * @param schematic the schematic to save
     * @throws IOException if the schematic could not be saved
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun save(schematic: SchematicFile, destination: File = schematic.source) {
        val nbt = CompoundTag()
        nbt.value["Width"] = ShortTag(schematic.xSize)
        nbt.value["Height"] = ShortTag(schematic.ySize)
        nbt.value["Length"] = ShortTag(schematic.zSize)

        val width = schematic.xSize.toInt()
        val height = schematic.ySize.toInt()
        val length = schematic.zSize.toInt()
        val totalBlocks = width * height * length

        val blockArray = ByteArray(totalBlocks)
        val dataArray = ByteArray(totalBlocks)

        for (y in 0..<height) {
            for (z in 0..<length) {
                for (x in 0..<width) {
                    val index = (y * length + z) * width + x
                    val chunkX = (x shr 4).toByte()
                    val chunkZ = (z shr 4).toByte()

                    val chunkKey = toKey(chunkX, chunkZ)
                    val blocksInChunk = schematic.chunkedBlocks[chunkKey]

                    if (blocksInChunk != null) {
                        var blockAtPosition: SchematicBlock? = null
                        for (block in blocksInChunk) {
                            if (block.x == x && block.y == y && block.z == z) {
                                blockAtPosition = block
                                break
                            }
                        }

                        if (blockAtPosition != null) {
                            blockArray[index] = blockAtPosition.type.toByte()
                            dataArray[index] = blockAtPosition.data
                            continue
                        }
                    }

                    blockArray[index] = 0
                    dataArray[index] = 0
                }
            }
        }

        val blocks = ByteArrayTag(*blockArray)
        val data = ByteArrayTag(*dataArray)

        nbt.value["Blocks"] = blocks
        nbt.value["Data"] = data

        val stream = NBTOutputStream(FileOutputStream(destination))
        stream.writeTag(nbt)
        stream.close()
    }
}