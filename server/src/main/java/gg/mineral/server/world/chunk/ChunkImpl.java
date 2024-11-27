package gg.mineral.server.world.chunk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;

import gg.mineral.api.world.World;
import gg.mineral.api.world.chunk.Chunk;
import gg.mineral.server.network.packet.play.clientbound.ChunkDataPacket;
import gg.mineral.server.util.collection.NibbleArray;
import gg.mineral.server.world.block.Block;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class ChunkImpl implements Chunk {

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
    private static final int ARRAY_SIZE = WIDTH * HEIGHT * SEC_DEPTH;

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

        private final byte[] types;
        private final NibbleArray metaData, skyLight, blockLight, addData;

        private int count;

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
            metaData = new NibbleArray(ARRAY_SIZE);
            skyLight = new NibbleArray(ARRAY_SIZE, (byte) 0xf);
            blockLight = new NibbleArray(ARRAY_SIZE);
            addData = new NibbleArray(ARRAY_SIZE);
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

    @Getter
    private final World world;
    /**
     * The coordinates of this chunk.
     */
    @Getter
    private final byte x, z;

    /**
     * The array of chunk sections this chunk contains, or null if it is unloaded.
     */
    private ChunkSection[] sections = new ChunkSection[DEPTH / SEC_DEPTH];

    /**
     * The array of biomes this chunk contains, or null if it is unloaded.
     */
    private byte[] biomes;

    private final ChunkDataPacket[] cache = new ChunkDataPacket[4];

    public void resetCache() {
        for (int i = 0; i < cache.length; i++)
            cache[i] = null;
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

    public Block getBlock(int x, short y, int z) {
        return new Block(this, (this.x << 4) | (x & 0xf), y & 0xff, (this.z << 4) |
                (z & 0xf), getType(x, z, y), getMetaData(x, z, y));
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
    public int getType(int x, int z, short y) {
        val section = getSection(y);
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
    public void setType(int x, int z, short y, int type) {
        if (type < 0 || type > 0xfff)
            return;

        resetCache();

        var section = getSection(y);
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

    public byte getMetaData(int x, int z, short y) {
        val section = getSection(y);
        return section == null ? 0 : section.metaData.get(section.index(x, y, z));
    }

    public void setMetaData(int x, int z, int y, byte metaData) {
        if (metaData < 0 || metaData >= 16)
            throw new IllegalArgumentException("Metadata out of range: " + metaData);

        resetCache();
        val section = getSection(y);
        if (section == null)
            return; // can't set metadata on an empty section
        section.metaData.set(section.index(x, y, z), metaData);
    }

    public byte getSkyLight(int x, int z, int y) {
        val section = getSection(y);
        return section == null ? 0 : section.skyLight.get(section.index(x, y, z));
    }

    public void setSkyLight(int x, int z, int y, int skyLight) {
        val section = getSection(y);
        if (section == null)
            return; // can't set light on an empty section
        resetCache();
        section.skyLight.set(section.index(x, y, z), (byte) skyLight);
    }

    public byte getBlockLight(int x, int z, int y) {
        val section = getSection(y);
        return section == null ? 0 : section.blockLight.get(section.index(x, y, z));
    }

    public void setBlockLight(int x, int z, int y, int blockLight) {
        val section = getSection(y);
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
        return toPacket(world.getEnvironment() == World.Environment.NORMAL, compress);
    }

    public ChunkDataPacket toPacket(boolean skylight, boolean compress) {
        val cache = getCache(skylight, compress);
        if (cache != null)
            return cache;

        boolean entireChunk = true; // TODO: don't send entire chunk if not needed

        int primaryBitmap = 0, addBitmap = 0, numSections = 0, numAddSections = 0;

        for (int i = 0; i < 16; i++) {
            val section = sections[i];
            if (section == null || section.isEmpty())
                continue;

            primaryBitmap |= 1 << i;
            numSections++;

            boolean hasAddData = false;
            for (int j = 0; j < section.types.length; j++) {
                int type = section.types[j] & 0xFF;
                if (type > 0xFF) {
                    hasAddData = true;
                    break;
                }
            }

            if (hasAddData) {
                addBitmap |= 1 << i;
                numAddSections++;
            }
        }

        int sizeBlockTypes = numSections * 4096, sizeMetadata = numSections * 2048, sizeBlockLight = numSections * 2048,
                sizeSkyLight = skylight ? numSections * 2048 : 0, sizeAddData = numAddSections * 2048,
                sizeBiomes = entireChunk ? 256 : 0;

        int totalSize = sizeBlockTypes + sizeMetadata + sizeBlockLight + sizeSkyLight + sizeAddData + sizeBiomes;

        val output = new byte[totalSize];

        int posBlockTypes = 0, posMetadata = posBlockTypes + sizeBlockTypes, posBlockLight = posMetadata + sizeMetadata,
                posSkyLight = posBlockLight + sizeBlockLight, posAddData = posSkyLight + sizeSkyLight,
                posBiomes = posAddData + sizeAddData;

        int sectionIndex = 0;
        for (int i = 0; i < 16; i++) {
            if ((primaryBitmap & (1 << i)) == 0)
                continue;

            var section = sections[i];
            if (section == null)
                section = EMPTY_SECTION;

            int offset = posBlockTypes + sectionIndex * 4096;
            System.arraycopy(section.types, 0, output, offset, 4096);

            sectionIndex++;
        }

        sectionIndex = 0;
        for (int i = 0; i < 16; i++) {
            if ((primaryBitmap & (1 << i)) == 0)
                continue;

            var section = sections[i];
            if (section == null)
                section = EMPTY_SECTION;

            int offset = posMetadata + sectionIndex * 2048;
            System.arraycopy(section.metaData.getRawData(), 0, output, offset, 2048);

            sectionIndex++;
        }

        sectionIndex = 0;
        for (int i = 0; i < 16; i++) {
            if ((primaryBitmap & (1 << i)) == 0)
                continue;

            var section = sections[i];
            if (section == null)
                section = EMPTY_SECTION;

            int offset = posBlockLight + sectionIndex * 2048;
            System.arraycopy(section.blockLight.getRawData(), 0, output, offset, 2048);

            sectionIndex++;
        }

        if (skylight) {
            sectionIndex = 0;
            for (int i = 0; i < 16; i++) {
                if ((primaryBitmap & (1 << i)) == 0)
                    continue;

                var section = sections[i];
                if (section == null)
                    section = EMPTY_SECTION;

                int offset = posSkyLight + sectionIndex * 2048;
                System.arraycopy(section.skyLight.getRawData(), 0, output, offset, 2048);

                sectionIndex++;
            }
        }

        if (addBitmap != 0) {
            sectionIndex = 0;
            for (int i = 0; i < 16; i++) {
                if ((addBitmap & (1 << i)) == 0)
                    continue;

                val section = sections[i];
                if (section == null)
                    continue;

                int offset = posAddData + sectionIndex * 2048;
                System.arraycopy(section.addData.getRawData(), 0, output, offset, 2048);

                sectionIndex++;
            }
        }

        if (entireChunk) {
            if (biomes == null)
                biomes = new byte[256];

            System.arraycopy(biomes, 0, output, posBiomes, 256);
        }

        val dataToSend = compress ? compress(output, totalSize) : output;
        val packet = new ChunkDataPacket(x, z, entireChunk, primaryBitmap, addBitmap, dataToSend);
        setCache(skylight, compress, packet);

        return packet;
    }

    public static byte[] compress(byte[] data, int length) {
        val outputStream = new ByteArrayOutputStream();
        try (val deflaterOutputStream = new DeflaterOutputStream(outputStream)) {
            deflaterOutputStream.write(data, 0, length);
        } catch (IOException e) {
            throw new RuntimeException("Failed to compress chunk data", e);
        }
        return outputStream.toByteArray();
    }

}