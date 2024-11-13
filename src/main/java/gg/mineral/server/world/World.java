package gg.mineral.server.world;

import gg.mineral.server.util.math.MathUtil;
import gg.mineral.server.world.chunk.Chunk;
import gg.mineral.server.world.chunk.EmptyChunk;
import lombok.Getter;

public class World implements IWorld {
    public static final byte MIN_CHUNK_COORD = -127, MAX_CHUNK_COORD = (byte) 128;
    @Getter
    final byte id;
    @Getter
    final String name;
    @Getter
    final Environment environment;
    @Getter
    final Generator generator;
    private Chunk[] chunkCache;

    public World(byte id, String name, Environment environment, Generator generator) {
        this.id = id;
        this.name = name;
        this.environment = environment == null ? Environment.NORMAL : environment;
        this.generator = generator;
        this.chunkCache = new Chunk[65536];
    }

    public World(byte id, String name, Environment environment) {
        this(id, name, environment, null);
    }

    public World(byte id, String name) {
        this(id, name, null, null);
    }

    public Chunk getChunk(short key) {
        var chunk = chunkCache[MathUtil.unsigned(key)];
        byte x = Chunk.xFromKey(key), z = Chunk.zFromKey(key);

        if (chunk == null)
            chunk = chunkCache[MathUtil.unsigned(key)] = generator != null ? generator.generate(environment, x, z)
                    : new EmptyChunk(environment, x, z);
        return chunk;
    }

    public int getType(int x, int y, int z) {
        return getChunk(Chunk.toKey((byte) (x >> 4), (byte) (z >> 4))).getType(x & 15, z & 15, y);
    }

    public int getMetaData(int x, int y, int z) {
        return getChunk(Chunk.toKey((byte) (x >> 4), (byte) (z >> 4))).getMetaData(x & 15, z & 15, y);
    }

}
