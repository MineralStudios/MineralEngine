package gg.mineral.server.tick

import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicInteger

class TickThreadFactory : ThreadFactory {
    private val threadCounter = AtomicInteger()

    override fun newThread(runnable: Runnable): Thread {
        val thread = Thread(runnable, "Mineral-tick-" + threadCounter.getAndIncrement())
        thread.priority = Thread.MAX_PRIORITY
        thread.uncaughtExceptionHandler =
            Thread.UncaughtExceptionHandler { t: Thread, e: Throwable ->
                System.err.println("Uncaught exception in thread " + t.name)
                e.printStackTrace()
            }
        return thread
    }

    companion object {
        val INSTANCE: TickThreadFactory = TickThreadFactory()
    }
}
