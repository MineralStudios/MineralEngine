package gg.mineral.server.tick;

import java.util.concurrent.TimeUnit;

import gg.mineral.api.tick.TickLoop;
import gg.mineral.server.MinecraftServerImpl;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class TickLoopImpl implements TickLoop {

    private int currentTicks;

    private static final int TPS = 20;
    private static final long SEC_IN_NANO = 1000000000;
    private static final int SAMPLE_INTERVAL = 20;

    private long tickSection, curTime;

    @Getter
    private RollingAverageImpl tps1, tps5, tps15;

    private final MinecraftServerImpl server;

    public static class RollingAverageImpl implements RollingAverage {
        private final int size;
        private long time;
        private double total;
        private int index = 0;
        private final double[] samples;
        private final long[] times;

        RollingAverageImpl(int size) {
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
        tps1 = new RollingAverageImpl(60);
        tps5 = new RollingAverageImpl(60 * 5);
        tps15 = new RollingAverageImpl(60 * 15);
        tickSection = System.nanoTime();

        server.getTickExecutor().scheduleAtFixedRate(() -> tick(), 50, 50,
                TimeUnit.MILLISECONDS);
    }

    public void tick() {
        try {
            val entities = server.getEntities().values();

            for (val entity : entities)
                entity.tick();

            server.getAsyncExecutor().invokeAll(entities);

            for (val connection : server.getConnections())
                connection.call();

            curTime = System.nanoTime();
            if (++currentTicks % SAMPLE_INTERVAL == 0) {
                final long diff = curTime - tickSection;
                double currentTps = 1E9 / diff * SAMPLE_INTERVAL;
                tps1.add(currentTps, diff);
                tps5.add(currentTps, diff);
                tps15.add(currentTps, diff);
                tickSection = curTime;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
