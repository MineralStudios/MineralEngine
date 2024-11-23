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
import gg.mineral.server.world.chunk.Chunk;
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

        short width = ((Short) nbt.getValue().get("Width").getValue()).shortValue();
        short height = ((Short) nbt.getValue().get("Height").getValue()).shortValue();
        short length = ((Short) nbt.getValue().get("Length").getValue()).shortValue();

        val schematic = new SchematicFile(source, width, height, length);

        val blockArray = (byte[]) nbt.getValue().get("Blocks").getValue();
        val dataArray = (byte[]) nbt.getValue().get("Data").getValue();

        val chunkedBlocks = schematic.getChunkedBlocks();

        for (int x = 0; x < width; x++) {
            int absoluteX = x;
            byte chunkX = (byte) (absoluteX >> 4);

            for (int y = 0; y < height; y++) {
                for (int z = 0; z < length; z++) {
                    int absoluteZ = z;
                    byte chunkZ = (byte) (absoluteZ >> 4);

                    int index = (y * length + z) * width + x;
                    int type = blockArray[index] & 0xFF;
                    byte data = (byte) (dataArray[index] & 0xF);

                    val block = new SchematicBlock(absoluteX, y, absoluteZ, type, data);

                    short chunkKey = Chunk.toKey(chunkX, chunkZ);

                    chunkedBlocks.computeIfAbsent(chunkKey, k -> new ArrayList<>()).add(block);
                }
            }
        }

        return schematic;
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

                    short chunkKey = Chunk.toKey(chunkX, chunkZ);
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