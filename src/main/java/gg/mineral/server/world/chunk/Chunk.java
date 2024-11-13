package gg.mineral.server.world.chunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.zip.DeflaterOutputStream;

import gg.mineral.server.network.packet.play.clientbound.ChunkDataPacket;
import gg.mineral.server.util.collection.NibbleArray;
import gg.mineral.server.world.IWorld.Environment;
import gg.mineral.server.world.World;
import gg.mineral.server.world.block.Block;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import lombok.val;

public class Chunk {

    @Getter
    private final IntSet entities = new IntOpenHashSet();

    private static final ChunkSection EMPTY_SECTION = new ChunkSection();

    /**
     * The dimensions of a chunk (width: x, height: z, depth: y).
     */
    public static final int WIDTH = 16, HEIGHT = 16, DEPTH = 256;

    /**
     * The Y depth of a single chunk section.
     */
    private static final int SEC_DEPTH = 16;

    public static short toKey(byte x, byte z) {
        return (short) ((x << 8) | (z & 0xFF));
    }

    public static byte xFromKey(short key) {
        return (byte) ((key >> 8) & 0xFF);
    }

    public static byte zFromKey(short key) {
        return (byte) (key & 0xFF);
    }

    public static final class ChunkSection {
        private static final int ARRAY_SIZE = WIDTH * HEIGHT * SEC_DEPTH;

        // these probably should be made non-public
        public final byte[] types, metaData;
        public final NibbleArray skyLight, blockLight;

        public int count;

        public void recount() {
            count = 0;
            for (byte type : types)
                if (type != 0)
                    count++;
        }

        /**
         * Create a new, empty ChunkSection.
         */
        public ChunkSection() {
            types = new byte[ARRAY_SIZE];
            metaData = new byte[ARRAY_SIZE];
            skyLight = new NibbleArray(ARRAY_SIZE, (byte) 0xf);
            blockLight = new NibbleArray(ARRAY_SIZE);
            recount();
        }

        public int index(int x, int y, int z) {
            if (x < 0 || z < 0 || x >= WIDTH || z >= HEIGHT)
                throw new IndexOutOfBoundsException("Coords (x=" + x + ",z=" + z + ") out of section bounds");

            return ((y & 0xf) << 8) | (z << 4) | x;
        }

        public boolean isEmpty() {
            for (byte type : types)
                if (type != 0)
                    return false;

            return true;
        }
    }

    /**
     * The coordinates of this chunk.
     */
    @Getter
    private final byte x, z;

    /**
     * The array of chunk sections this chunk contains, or null if it is unloaded.
     */
    private ChunkSection[] sections = new ChunkSection[DEPTH / SEC_DEPTH];

    @Getter
    private Environment environment;

    /**
     * The array of biomes this chunk contains, or null if it is unloaded.
     */
    private byte[] biomes;

    /**
     * Creates a new chunk with a specified X and Z coordinate.
     * 
     * @param x The X coordinate.
     * @param z The Z coordinate.
     */
    public Chunk(Environment environment, byte x, byte z) {
        this.environment = environment;
        this.x = x;
        this.z = z;
    }

    private ChunkDataPacket[] cache = new ChunkDataPacket[4];

    public void resetCache() {
        cache = new ChunkDataPacket[4];
    }

    public ChunkDataPacket getCache(boolean skylight, boolean compress) {
        int index;
        if (skylight && compress)
            index = 3;
        else if (skylight)
            index = 2;
        else if (compress)
            index = 1;
        else
            index = 0;
        return cache[index];
    }

    public void setCache(boolean skylight, boolean compress, ChunkDataPacket packet) {
        int index;
        if (skylight && compress)
            index = 3;
        else if (skylight)
            index = 2;
        else if (compress)
            index = 1;
        else
            index = 0;
        cache[index] = packet;
    }

    public void generateCache() {
        toPacket(true, true);
        toPacket(true, false);
        toPacket(false, true);
        toPacket(false, false);
    }

    // ======== Basic stuff ========

    public Block getBlock(int x, int y, int z) {
        return new Block(this, (this.x << 4) | (x & 0xf), y & 0xff, (this.z << 4) |
                (z & 0xf));
    }

    // ======== Data access ========

    /**
     * Attempt to get the ChunkSection at the specified height.
     * 
     * @param y the y value.
     * @return The ChunkSection, or null if it is empty.
     */
    private ChunkSection getSection(int y) {
        int idx = y >> 4;
        if (y < 0 || y >= DEPTH || idx >= sections.length)
            return null;

        return sections[idx];
    }

    /**
     * Gets the type of a block within this chunk.
     * 
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The type.
     */
    public int getType(int x, int z, int y) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : (section.types[section.index(x, y, z)] & 0xff);
    }

    /**
     * Sets the type of a block within this chunk.
     * 
     * @param x    The X coordinate.
     * @param z    The Z coordinate.
     * @param y    The Y coordinate.
     * @param type The type.
     */
    public void setType(int x, int z, int y, int type) {
        if (type < 0 || type > 0xfff)
            throw new IllegalArgumentException("Block type out of range: " + type);

        resetCache();

        ChunkSection section = getSection(y);
        if (section == null) {
            if (type == 0) {
                // don't need to create chunk for air
                return;
            } else {
                // create new ChunkSection for this y coordinate
                int idx = y >> 4;
                if (y < 0 || y >= DEPTH || idx >= sections.length) {
                    // y is out of range somehow
                    return;
                }
                sections[idx] = section = new ChunkSection();
            }
        }

        // update the air count and height map
        int index = section.index(x, y, z);
        if (type == 0) {
            if (section.types[index] != 0)
                section.count--;

        } else {
            if (section.types[index] == 0)
                section.count++;

        }
        // update the type - also sets metadata to 0
        section.types[section.index(x, y, z)] = (byte) type;

        if (type == 0 && section.count == 0) {
            // destroy the empty section
            sections[y / SEC_DEPTH] = null;
            return;
        }
    }

    /**
     * Gets the metadata of a block within this chunk.
     * 
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The metadata.
     */
    public int getMetaData(int x, int z, int y) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.metaData[section.index(x, y, z)];
    }

    /**
     * Sets the metadata of a block within this chunk.
     * 
     * @param x        The X coordinate.
     * @param z        The Z coordinate.
     * @param y        The Y coordinate.
     * @param metaData The metadata.
     */
    public void setMetaData(int x, int z, int y, int metaData) {
        if (metaData < 0 || metaData >= 16)
            throw new IllegalArgumentException("Metadata out of range: " + metaData);

        resetCache();
        ChunkSection section = getSection(y);
        if (section == null)
            return; // can't set metadata on an empty section
        section.metaData[section.index(x, y, z)] = (byte) metaData;
    }

    /**
     * Gets the sky light level of a block within this chunk.
     * 
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The sky light level.
     */
    public byte getSkyLight(int x, int z, int y) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.skyLight.get(section.index(x, y, z));
    }

    /**
     * Sets the sky light level of a block within this chunk.
     * 
     * @param x        The X coordinate.
     * @param z        The Z coordinate.
     * @param y        The Y coordinate.
     * @param skyLight The sky light level.
     */
    public void setSkyLight(int x, int z, int y, int skyLight) {
        ChunkSection section = getSection(y);
        if (section == null)
            return; // can't set light on an empty section
        resetCache();
        section.skyLight.set(section.index(x, y, z), (byte) skyLight);
    }

    /**
     * Gets the block light level of a block within this chunk.
     * 
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @param y The Y coordinate.
     * @return The block light level.v
     */
    public byte getBlockLight(int x, int z, int y) {
        ChunkSection section = getSection(y);
        return section == null ? 0 : section.blockLight.get(section.index(x, y, z));
    }

    /**
     * Sets the block light level of a block within this chunk.
     * 
     * @param x          The X coordinate.
     * @param z          The Z coordinate.
     * @param y          The Y coordinate.
     * @param blockLight The block light level.
     */
    public void setBlockLight(int x, int z, int y, int blockLight) {
        ChunkSection section = getSection(y);
        if (section == null)
            return; // can't set light on an empty section
        resetCache();
        section.blockLight.set(section.index(x, y, z), (byte) blockLight);
    }

    /**
     * Gets the biome of a column within this chunk.
     * 
     * @param x The X coordinate.
     * @param z The Z coordinate.
     * @return The biome.
     */
    public int getBiome(int x, int z) {
        if (biomes == null)
            return 0;
        return biomes[z * WIDTH + x] & 0xFF;
    }

    /**
     * Sets the biome of a column within this chunk,
     * 
     * @param x     The X coordinate.
     * @param z     The Z coordinate.
     * @param biome The biome.
     */
    public void setBiome(int x, int z, int biome) {
        if (biomes == null)
            return;
        resetCache();
        biomes[z * WIDTH + x] = (byte) biome;
    }

    /**
     * Set the entire biome array of this chunk.
     * 
     * @param newBiomes The biome array.
     */
    public void setBiomes(byte[] newBiomes) {
        if (biomes == null)
            throw new IllegalStateException("Must initialize chunk first");

        if (newBiomes.length != biomes.length)
            throw new IllegalArgumentException("Biomes array not of length " + biomes.length);
        resetCache();

        System.arraycopy(newBiomes, 0, biomes, 0, biomes.length);
    }

    public ChunkDataPacket toPacket(boolean compress) {
        return toPacket(environment == World.Environment.NORMAL, compress);
    }

    public ChunkDataPacket toPacket(boolean skylight, boolean compress) {

        val cache = getCache(skylight, compress);
        if (cache != null)
            return cache;

        boolean entireChunk = true;
        int primaryBitmap = 0, addBitmap = 0;

        byte[] output = new byte[196864];
        int outputPos = 0;

        for (int i = 0; i < sections.length; i++) {
            val section = sections[i];

            if (section == null)
                continue;

            boolean foundBlock = false;
            boolean foundAdd = false;

            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    for (int x = 0; x < 16; x++) {
                        int index = section.index(x, y, z);
                        int type = section.types[index] & 0xff;
                        int metaData = section.metaData[index] & 0xff;
                        // int blockLight = section.blockLight[index] & 0xff;
                        // int skyLight = section.skyLight[index] & 0xff;

                        if (type != 0 || metaData != 0)
                            foundBlock = true;

                        if ((type & 0xf00) != 0)
                            foundAdd = true;

                    }
                }
            }

            if (foundBlock)
                primaryBitmap |= 1 << i;

            if (foundAdd)
                addBitmap |= 1 << i;
        }

        int mask = primaryBitmap | addBitmap;
        // int fullSectionsCount = Integer.bitCount(mask);

        for (int i = 0; i < 16; i++) {
            if ((mask & (1 << i)) == 0)
                continue;

            ChunkSection section = sections[i];
            if (section == null)
                section = EMPTY_SECTION;
            System.arraycopy(section.types, 0, output, outputPos, 4096);
            outputPos += 4096;
        }

        for (int i = 0; i < 16; i++) {
            if ((primaryBitmap & (1 << i)) == 0)
                continue;

            ChunkSection section = sections[i];

            if (section == null)
                section = EMPTY_SECTION;

            for (int j = 0; j < 2048; j++)
                output[outputPos++] = (byte) ((section.metaData[j << 1] << 4) | (section.metaData[(j << 1) + 1] & 0xf));

            System.arraycopy(section.blockLight.toByteArray(), 0, output, outputPos, 2048);
            outputPos += 2048;

            if (skylight) {
                System.arraycopy(section.skyLight.toByteArray(), 0, output, outputPos, 2048);
                outputPos += 2048;
            }
        }

        if (entireChunk) {
            if (biomes == null)
                biomes = new byte[256];

            System.arraycopy(biomes, 0, output, outputPos, 256);
            outputPos += 256;
        }

        byte[] compressedData = compress ? compress(output, outputPos) : Arrays.copyOf(output, outputPos);

        ChunkDataPacket packet = new ChunkDataPacket(x, z, primaryBitmap, addBitmap, entireChunk, compressedData);

        setCache(skylight, compress, packet);

        return packet;
    }

    public static byte[] compress(byte[] data, int length) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(outputStream)) {
            deflaterOutputStream.write(data, 0, length);
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress chunk data", e);
        }
        return outputStream.toByteArray();
    }

}