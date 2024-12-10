package gg.mineral.server.tick

import gg.mineral.api.tick.TickLoop
import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.entity.EntityImpl
import it.unimi.dsi.fastutil.objects.ObjectCollection
import lombok.Getter
import java.util.concurrent.TimeUnit

class TickLoopImpl(private val server: MinecraftServerImpl) : TickLoop {
    private var currentTicks = 0
    private var tickSection: Long = 0
    private var curTime: Long = 0

    @Getter
    private var tps1: RollingAverageImpl? = null

    @Getter
    private var tps5: RollingAverageImpl? = null

    @Getter
    private var tps15: RollingAverageImpl? = null

    fun start() {
        tps1 = RollingAverageImpl(60)
        tps5 = RollingAverageImpl(60 * 5)
        tps15 = RollingAverageImpl(60 * 15)
        tickSection = System.nanoTime()

        server.tickExecutor.scheduleAtFixedRate(
            { tick() }, 50, 50,
            TimeUnit.MILLISECONDS
        )
    }

    fun tick() {
        try {
            val entities: ObjectCollection<EntityImpl> = server.entities.values

            for (entity in entities) entity.tick()

            server.asyncExecutor.invokeAll(entities)

            for (connection in server.connections) connection.call()

            curTime = System.nanoTime()
            if (++currentTicks % SAMPLE_INTERVAL == 0) {
                val diff = curTime - tickSection
                val currentTps = 1E9 / diff * SAMPLE_INTERVAL
                tps1!!.add(currentTps, diff)
                tps5!!.add(currentTps, diff)
                tps15!!.add(currentTps, diff)
                tickSection = curTime
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    class RollingAverageImpl internal constructor(private val size: Int) : TickLoop.RollingAverage {
        private val samples = DoubleArray(size)
        private val times = LongArray(size)

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

        override fun getAverage(): Double {
            return total / time
        }
    }

    companion object {
        private const val TPS = 20
        private const val SEC_IN_NANO: Long = 1000000000
        private const val SAMPLE_INTERVAL = 20
    }
}
