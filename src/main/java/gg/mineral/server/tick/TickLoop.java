package gg.mineral.server.tick;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

import gg.mineral.server.MinecraftServer;
import gg.mineral.server.entity.Entity;
import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.connection.Connection;

public class TickLoop {

    private int[] currentTicks;

    public static final int TPS = 20;
    private static final long SEC_IN_NANO = 1000000000;
    public static final long TICK_EVERY = SEC_IN_NANO / 20;
    private static final int SAMPLE_INTERVAL = 20;

    long[] tickSection, curTime;

    public RollingAverage[] tps1, tps5, tps15;

    public static class RollingAverage {
        private final int size;
        private long time;
        private double total;
        private int index = 0;
        private final double[] samples;
        private final long[] times;

        RollingAverage(int size) {
            this.size = size;
            this.time = size * SEC_IN_NANO;
            this.total = TPS * SEC_IN_NANO * size;
            this.samples = new double[size];
            this.times = new long[size];
            for (int i = 0; i < size; i++) {
                this.samples[i] = TPS;
                this.times[i] = SEC_IN_NANO;
            }
        }

        public void add(double x, long t) {
            time -= times[index];
            total -= samples[index] * times[index];
            samples[index] = x;
            times[index] = t;
            time += t;
            total += x * t;
            if (++index == size)
                index = 0;
        }

        public double getAverage() {
            return total / time;
        }
    }

    public void start() {
        int threadCount = MinecraftServer.getTickThreadCount();
        currentTicks = new int[threadCount];
        tickSection = new long[threadCount];
        curTime = new long[threadCount];
        tps1 = new RollingAverage[threadCount];
        tps5 = new RollingAverage[threadCount];
        tps15 = new RollingAverage[threadCount];

        for (int i = 0; i < threadCount; i++) {
            tps1[i] = new RollingAverage(60);
            tps5[i] = new RollingAverage(60 * 5);
            tps15[i] = new RollingAverage(60 * 15);
            tickSection[i] = System.nanoTime();
        }

        for (int i = 0; i < threadCount; i++)
            startThread(i);
    }

    public void startThread(final int i) {
        MinecraftServer.getTickExecutor().scheduleAtFixedRate(() -> tick(i), 0, TICK_EVERY, TimeUnit.NANOSECONDS);
    }

    public void tick(int threadNumber) {
        int threadCount = MinecraftServer.getTickThreadCount();

        Collection<Entity> entities = EntityManager.getEntities().values();

        int index = 0;
        for (Entity entity : entities)
            if (index++ % threadCount == threadNumber)
                entity.tick();

        index = 0;
        for (Connection connection : Connection.LIST)
            if (index++ % threadCount == threadNumber)
                connection.tick();

        curTime[threadNumber] = System.nanoTime();
        if (++currentTicks[threadNumber] % SAMPLE_INTERVAL == 0) {
            final long diff = curTime[threadNumber] - tickSection[threadNumber];
            double currentTps = 1E9 / diff * SAMPLE_INTERVAL;
            tps1[threadNumber].add(currentTps, diff);
            tps5[threadNumber].add(currentTps, diff);
            tps15[threadNumber].add(currentTps, diff);
            tickSection[threadNumber] = curTime[threadNumber];
        }
    }
}
