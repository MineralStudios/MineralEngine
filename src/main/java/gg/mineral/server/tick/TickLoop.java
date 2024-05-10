package gg.mineral.server.tick;

import java.util.concurrent.TimeUnit;

import gg.mineral.server.MinecraftServer;
import gg.mineral.server.entity.Entity;
import gg.mineral.server.entity.manager.EntityManager;
import lombok.Getter;

public class TickLoop {

    @Getter
    int[] currentTicks;

    public static final int TICK_EVERY = 50;

    public void start() {
        currentTicks = new int[MinecraftServer.getThreadCount()];
        for (int i = 0; i < MinecraftServer.getThreadCount(); i++)
            startThread(i);
    }

    public void startThread(final int i) {
        MinecraftServer.getExecutor().scheduleAtFixedRate(() -> tick(i), 0, TICK_EVERY, TimeUnit.MILLISECONDS);
    }

    public void tick(int threadNumber) {
        currentTicks[threadNumber]++;
        Entity[] entities = EntityManager.getEntities().values().toArray(new Entity[0]);
        int entityCount = entities.length;
        int threadCount = MinecraftServer.getThreadCount();

        int size = entityCount / threadCount;
        int start = size * threadNumber;
        int end = (threadNumber == threadCount - 1) ? entityCount : (threadNumber + 1) * size;

        for (int i = start; i < end; i++) {
            Entity entity = entities[i];
            if (entity != null)
                entity.tick(threadNumber);
        }
    }
}
