package gg.mineral.server.world.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import gg.mineral.server.util.nbt.ByteArrayTag;
import gg.mineral.server.util.nbt.CompoundTag;
import gg.mineral.server.util.nbt.NBTInputStream;
import gg.mineral.server.util.nbt.NBTOutputStream;
import gg.mineral.server.util.nbt.ShortTag;
import gg.mineral.server.world.chunk.ChunkImpl;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

public class Schematic {

    /**
     * Loads a schematic from a source file.
     *
     * @param source the source file
     * @return the loaded schematic
     */
    @SneakyThrows
    public static SchematicFile load(File source) {
        @Cleanup
        val stream = new NBTInputStream(new FileInputStream(source));
        val nbt = stream.readCompound();

        val nbtValue = nbt.getValue();

        if (nbtValue == null)
            throw new IllegalArgumentException("Invalid schematic file: missing root tag");

        val widthTag = nbtValue.get("Width");
        val heightTag = nbtValue.get("Height");
        val lengthTag = nbtValue.get("Length");

        if (widthTag == null || heightTag == null || lengthTag == null)
            throw new IllegalArgumentException("Invalid schematic file: missing dimensions");

        if (widthTag.getValue() instanceof Short width && heightTag.getValue() instanceof Short height
                && lengthTag.getValue() instanceof Short length) {

            val schematic = new SchematicFile(source, width, height, length);

            val blocksTag = nbtValue.get("Blocks");
            val dataTag = nbtValue.get("Data");

            if (blocksTag == null || dataTag == null)
                throw new IllegalArgumentException("Invalid schematic file: missing blocks");

            if (blocksTag.getValue() instanceof byte[] blockArray && dataTag.getValue() instanceof byte[] dataArray) {
                val chunkedBlocks = schematic.getChunkedBlocks();

                for (int x = 0; x < width; x++) {
                    int absoluteX = x;
                    byte chunkX = (byte) (absoluteX >> 4);

                    for (int y = 0; y < height; y++) {
                        for (int z = 0; z < length; z++) {
                            int absoluteZ = z;
                            byte chunkZ = (byte) (absoluteZ >> 4);

                            int index = (y * length + z) * width + x, type = blockArray[index] & 0xFF;
                            byte data = (byte) (dataArray[index] & 0xF);

                            val block = new SchematicBlock(absoluteX, y, absoluteZ, type, data);

                            short chunkKey = ChunkImpl.toKey(chunkX, chunkZ);

                            chunkedBlocks.computeIfAbsent(chunkKey, k -> new ArrayList<>()).add(block);
                        }
                    }
                }

                return schematic;
            }
        }

        throw new IllegalArgumentException("Invalid schematic file: invalid dimensions");
    }

    /**
     * Saves a schematic file to its source file
     *
     * @param schematic the schematic to save
     * @throws IOException if the schematic could not be saved
     */
    public static void save(SchematicFile schematic) throws IOException {
        save(schematic, schematic.getSource());
    }

    /**
     * Saves a schematic file to the given destination
     *
     * @param schematic the schematic to save
     * @throws IOException if the schematic could not be saved
     */
    public static void save(SchematicFile schematic, File destination) throws IOException {
        val nbt = new CompoundTag();
        nbt.getValue().put("Width", new ShortTag(schematic.getXSize()));
        nbt.getValue().put("Height", new ShortTag(schematic.getYSize()));
        nbt.getValue().put("Length", new ShortTag(schematic.getZSize()));

        int width = schematic.getXSize(), height = schematic.getYSize(), length = schematic.getZSize(),
                totalBlocks = width * height * length;

        val blockArray = new byte[totalBlocks];
        val dataArray = new byte[totalBlocks];

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < length; z++) {
                for (int x = 0; x < width; x++) {
                    int index = (y * length + z) * width + x;
                    int absoluteX = x;
                    int absoluteZ = z;
                    byte chunkX = (byte) (absoluteX >> 4);
                    byte chunkZ = (byte) (absoluteZ >> 4);

                    short chunkKey = ChunkImpl.toKey(chunkX, chunkZ);
                    val blocksInChunk = schematic.getChunkedBlocks().get(chunkKey);

                    if (blocksInChunk != null) {
                        SchematicBlock blockAtPosition = null;
                        for (val block : blocksInChunk) {
                            if (block.getX() == absoluteX && block.getY() == y && block.getZ() == absoluteZ) {
                                blockAtPosition = block;
                                break;
                            }
                        }

                        if (blockAtPosition != null) {
                            blockArray[index] = (byte) blockAtPosition.getType();
                            dataArray[index] = blockAtPosition.getData();
                            continue;
                        }
                    }

                    blockArray[index] = 0;
                    dataArray[index] = 0;
                }
            }
        }

        val blocks = new ByteArrayTag(blockArray);
        val data = new ByteArrayTag(dataArray);

        nbt.getValue().put("Blocks", blocks);
        nbt.getValue().put("Data", data);

        val stream = new NBTOutputStream(new FileOutputStream(destination));
        stream.writeTag(nbt);
        stream.close();
    }
}