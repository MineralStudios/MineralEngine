package gg.mineral.server.tick

import gg.mineral.api.tick.TickLoop
import gg.mineral.server.MinecraftServerImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

open class TickLoopImpl(
    val server: MinecraftServerImpl,
    val executor: ScheduledExecutorService = server.executor,
    val scope: CoroutineScope = CoroutineScope(executor.asCoroutineDispatcher())
) : TickLoop {
    private var currentTicks = 0
    private var tickSection: Long = 0
    private var curTime: Long = 0

    override var tps1: RollingAverageImpl = RollingAverageImpl(60)
    override var tps5: RollingAverageImpl = RollingAverageImpl(60 * 5)
    override var tps15: RollingAverageImpl = RollingAverageImpl(60 * 15)

    fun start() {
        tickSection = System.nanoTime()

        executor.scheduleAtFixedRate(
            {
                try {
                    tick()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 50, 50,
            TimeUnit.MILLISECONDS
        )
    }

    open fun tick() {
        try {
            curTime = System.nanoTime()
            if (++currentTicks % SAMPLE_INTERVAL == 0) {
                val diff = curTime - tickSection
                val currentTps = 1E9 / diff * SAMPLE_INTERVAL
                tps1.add(currentTps, diff)
                tps5.add(currentTps, diff)
                tps15.add(currentTps, diff)
                tickSection = curTime
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class RollingAverageImpl internal constructor(private val size: Int) : TickLoop.RollingAverage {
        private val samples = DoubleArray(size)
        private val times = LongArray(size)
        override val average: Double
            get() = total / time

        private var time = size * SEC_IN_NANO
        private var total = (TPS * SEC_IN_NANO * size).toDouble()
        private var index = 0

        init {
            for (i in 0..<size) {
                samples[i] = TPS.toDouble()
                times[i] = SEC_IN_NANO
            }
        }

        fun add(x: Double, t: Long) {
            time -= times[index]
            total -= samples[index] * times[index]
            samples[index] = x
            times[index] = t
            time += t
            total += x * t
            if (++index == size) index = 0
        }
    }

    companion object {
        private const val TPS = 20
        private const val SEC_IN_NANO: Long = 1000000000
        private const val SAMPLE_INTERVAL = 20
    }
}
