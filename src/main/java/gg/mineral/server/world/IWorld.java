package gg.mineral.server.world;

import gg.mineral.server.world.chunk.Chunk;

public interface IWorld {

    byte getId();

    String getName();

    Environment getEnvironment();

    Generator getGenerator();

    public static interface Generator {
        Chunk generate(Environment environment, byte chunkX, byte chunkZ);
    }

    public static enum Environment {
        NORMAL, NETHER, END
    }

}
