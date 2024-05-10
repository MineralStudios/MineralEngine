package gg.mineral.server.tick;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class TickThreadFactory implements ThreadFactory {
    public static final TickThreadFactory INSTANCE = new TickThreadFactory();
    private final AtomicInteger threadCounter = new AtomicInteger();

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, "Mineral-tick-" + threadCounter.getAndIncrement());
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setUncaughtExceptionHandler((t, e) -> {
            System.err.println("Uncaught exception in thread " + t.getName());
            e.printStackTrace();
        });
        return thread;
    }
}
