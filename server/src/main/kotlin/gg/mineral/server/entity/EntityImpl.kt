package gg.mineral.server.entity

import gg.mineral.api.entity.Entity
import gg.mineral.api.math.MathUtil.cos
import gg.mineral.api.math.MathUtil.sin
import gg.mineral.api.math.MathUtil.toPitchUnits
import gg.mineral.api.math.MathUtil.toRadians
import gg.mineral.api.math.MathUtil.toSoundUnits
import gg.mineral.api.math.MathUtil.toVelocityUnits
import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.command.impl.KnockbackCommand
import gg.mineral.server.entity.living.HumanImpl
import gg.mineral.server.entity.living.human.PlayerImpl
import gg.mineral.server.network.packet.play.clientbound.EntityStatusPacket
import gg.mineral.server.network.packet.play.clientbound.EntityVelocityPacket
import gg.mineral.server.network.packet.play.clientbound.SoundEffectPacket
import gg.mineral.server.world.WorldImpl
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sqrt

abstract class EntityImpl(override val id: Int, world: WorldImpl) : Callable<EntityImpl>,
    Entity {

    override var world = world
        set(value) {
            val oldWorld: WorldImpl = field

            if (oldWorld === world) return

            oldWorld.removeEntity(this.id)
            world.addEntity(this)
            field = value

            if (this is PlayerImpl) this.visibleChunks.clear()
        }

    override var x: Double = 0.0
    override var y: Double = 0.0
    override var z: Double = 0.0
    override var motX: Double = 0.0
    override var motY: Double = 0.0
    override var motZ: Double = 0.0
    override var headY: Double = 0.0
    override var yaw: Float = 0f
        set(value) {
            lastYaw = field
            field = value
        }

    override var pitch: Float = 0f
        set(value) {
            lastPitch = field
            field = value
        }

    private var lastYaw: Float = 0f
    private var lastPitch: Float = 0f
    override var onGround: Boolean = false
    override val viewDistance = 10.toByte() // TODO: viewDistance
    override var currentTick = 0
    var firstTick = true
    var firstAsyncTick = true
    var chunkUpdateNeeded = true
    private var lastDamaged = 0

    @JvmField
    protected var width: Float = 0f

    @JvmField
    protected var height: Float = 0f

    override val server: MinecraftServerImpl by lazy { world.server }

    init {
        world.addEntity(this)
    }

    private val random: Random
        get() = ThreadLocalRandom.current()

    open fun tick() {
        currentTick++

        if (motY < 0.005) motY = 0.0

        if (motX < 0.005) motX = 0.0

        if (motZ < 0.005) motZ = 0.0

        if (motY > 0) {
            motY -= 0.08
            motY *= 0.98
        }

        if (motX > 0) {
            motX *= 0.91
            if (onGround) motX *= 0.6
        }

        if (motZ > 0) {
            motZ *= 0.91
            if (onGround) motZ *= 0.6
        }
    }

    abstract fun tickAsync()

    override fun call(): EntityImpl {
        tickAsync()
        return this
    }

    override fun attack(targetId: Int) {
        val entity = server.entities[targetId]

        if (entity == null || entity.currentTick - entity.lastDamaged < 10) return

        entity.lastDamaged = entity.currentTick
        val statusPacket = EntityStatusPacket(entity.id, 2.toByte())

        var motX: Double = entity.motX
        var motY: Double = entity.motY
        var motZ: Double = entity.motZ
        val x = KnockbackCommand.x
        val y = KnockbackCommand.y
        val z = KnockbackCommand.z

        val extraX = KnockbackCommand.extraX
        val extraY = KnockbackCommand.extraY
        val extraZ = KnockbackCommand.extraZ

        val yLimit = KnockbackCommand.yLimit

        val friction = KnockbackCommand.friction

        if (friction > 0) {
            motX /= friction
            motY /= friction
            motZ /= friction
        } else {
            motX = 0.0
            motY = 0.0
            motZ = 0.0
        }

        val distanceX = this.x - entity.x
        val distanceZ = this.z - entity.z

        val magnitude = sqrt(distanceX * distanceX + distanceZ * distanceZ)

        motX -= distanceX / magnitude * x
        motY += y
        motZ -= distanceZ / magnitude * z

        if (motY > yLimit) motY = yLimit

        if (this is HumanImpl) {
            if (this.extraKnockback) {
                val angle = toRadians(this.yaw)
                val sin = -sin(angle.toDouble())
                val cos = cos(angle.toDouble())
                motX += extraX * sin
                motY += extraY
                motZ += extraZ * cos
                this.motX *= 0.6
                this.motZ *= 0.6
                this.extraKnockback = false
            }
        }
        val velocityPacket = EntityVelocityPacket(
            targetId, toVelocityUnits(motX),
            toVelocityUnits(motY), toVelocityUnits(motZ)
        )

        if (entity is PlayerImpl) entity.connection.queuePacket(statusPacket, velocityPacket)

        if (this is PlayerImpl) {
            val player = this
            player.connection.queuePacket(
                statusPacket, SoundEffectPacket(
                    "game.player.hurt",
                    toSoundUnits(entity.x), toSoundUnits(entity.y),
                    toSoundUnits(entity.z), 1.0f, toPitchUnits(
                        ((random.nextFloat()
                                - random.nextFloat()) * 0.2f + 1.0f).toDouble()
                    )
                )
            )
        }
        entity.motX = motX
        entity.motY = motY
        entity.motZ = motZ
    }
}
