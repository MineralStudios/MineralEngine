package gg.mineral.server.world;

import java.util.concurrent.ConcurrentHashMap;
import gg.mineral.server.world.chunk.Chunk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class World {
    public static final short MIN_COORD = -2047, MAX_COORD = 2048;
    @Getter
    final byte id;
    @Getter
    final String name;
    @Getter
    final Environment environment;
    @Getter
    final Generator generator;
    // use concurrent map to ensure everything is done atomically
    ConcurrentHashMap<Short, Chunk> chunkCache = new ConcurrentHashMap<>();

    public static enum Environment {
        NORMAL, NETHER, END
    }

    public Chunk getChunk(short key) {
        return chunkCache.computeIfAbsent(key, k -> generator.generate(this, Chunk.xFromKey(k), Chunk.zFromKey(k)));
    }

    public int getType(int x, int y, int z) {
        return getChunk(Chunk.toKey((byte) (x >> 4), (byte) (z >> 4))).getType(x & 15, z & 15, y);
    }

    public int getMetaData(int x, int y, int z) {
        return getChunk(Chunk.toKey((byte) (x >> 4), (byte) (z >> 4))).getMetaData(x & 15, z & 15, y);
    }

    public static interface Generator {
        Chunk generate(World world, byte chunkX, byte chunkZ);
    }

}
