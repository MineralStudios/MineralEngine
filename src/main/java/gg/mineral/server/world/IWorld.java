package gg.mineral.server.world;

import gg.mineral.server.entity.Entity;
import gg.mineral.server.entity.living.human.Player;
import gg.mineral.server.world.chunk.Chunk;

import java.util.List;

public interface IWorld {

    byte getId();

    String getName();

    Environment getEnvironment();

    Generator getGenerator();

    public static interface Generator {
        Chunk generate(IWorld world, byte chunkX, byte chunkZ);
    }

    public static enum Environment {
        NORMAL, NETHER, END
    }

    Chunk getChunk(short key);

    Entity getEntity(int intKey);

    Player getPlayer(int entityId);

    int getType(int x, short y, int z);

    int getMetaData(int x, short y, int z);

    void removeEntity(int id);

    void addEntity(Entity entity);

    void updatePosition(Entity entity);

    void updateChunks(Player player);

    List<Chunk> getChunkLoadUpdates(Player player);
}
