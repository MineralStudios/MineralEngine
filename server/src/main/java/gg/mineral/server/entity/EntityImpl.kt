package gg.mineral.server.entity

import gg.mineral.api.entity.Entity
import gg.mineral.api.math.MathUtil
import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.command.impl.KnockbackCommand
import gg.mineral.server.entity.living.HumanImpl
import gg.mineral.server.entity.living.human.PlayerImpl
import gg.mineral.server.network.packet.play.clientbound.EntityStatusPacket
import gg.mineral.server.network.packet.play.clientbound.EntityVelocityPacket
import gg.mineral.server.network.packet.play.clientbound.SoundEffectPacket
import gg.mineral.server.world.WorldImpl
import lombok.Getter
import lombok.Setter
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ThreadLocalRandom
import kotlin.math.sqrt

@Getter
abstract class EntityImpl(protected val id: Int, world: WorldImpl) : Callable<EntityImpl>,
    Entity, MathUtil {
    @Setter
    protected var x: Double = 0.0

    @Setter
    protected var y: Double = 0.0

    @Setter
    protected var z: Double = 0.0

    @Setter
    protected var motX: Double = 0.0

    @Setter
    protected var motY: Double = 0.0

    @Setter
    protected var motZ: Double = 0.0

    @Setter
    protected var headY: Double = 0.0
    protected var yaw: Float = 0f
    protected var pitch: Float = 0f
    protected var lastYaw: Float = 0f
    protected var lastPitch: Float = 0f

    @Setter
    protected var onGround: Boolean = false

    @Getter
    private val viewDistance = 10.toByte()

    @Getter
    private var currentTick = 0

    @Getter
    @Setter
    private val firstTick = true

    @Getter
    @Setter
    private val firstAsyncTick = true

    @Getter
    @Setter
    private val chunkUpdateNeeded = true

    @Getter
    @Setter
    private val lastDamaged = 0
    protected var width: Float = 0f
    protected var height: Float = 0f

    @Getter
    protected var world: WorldImpl? = null
    protected val server: MinecraftServerImpl

    init {
        this.setWorld(world)
        this.server = world.server
    }

    val random: Random
        get() = ThreadLocalRandom.current()

    open fun setWorld(world: WorldImpl) {
        val oldWorld: WorldImpl? = getWorld()

        if (oldWorld === world) return

        oldWorld?.removeEntity(this.getId())
        world.addEntity(this)
        this.world = world
    }

    override fun setYaw(yaw: Float) {
        lastYaw = this.yaw
        this.yaw = yaw
    }

    override fun setPitch(pitch: Float) {
        lastPitch = this.pitch
        this.pitch = pitch
    }

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

        if (entity == null || entity.getCurrentTick() - entity.getLastDamaged() < 10) return

        entity.setLastDamaged(entity.getCurrentTick())
        val statusPacket = EntityStatusPacket(entity.getId(), 2.toByte())

        var motX: Double = entity.getMotX()
        var motY: Double = entity.getMotY()
        var motZ: Double = entity.getMotZ()
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

        val distanceX = this.getX() - entity.getX()
        val distanceZ = this.getZ() - entity.getZ()

        val magnitude = sqrt(distanceX * distanceX + distanceZ * distanceZ)

        motX -= distanceX / magnitude * x
        motY += y
        motZ -= distanceZ / magnitude * z

        if (motY > yLimit) motY = yLimit

        if (this is HumanImpl) {
            if (human.isExtraKnockback()) {
                val angle = toRadians(this.getYaw())
                val sin = -sin(angle.toDouble())
                val cos = cos(angle.toDouble())
                motX += extraX * sin
                motY += extraY
                motZ += extraZ * cos
                this.setMotX(this.getMotX() * 0.6)
                this.setMotZ(this.getMotZ() * 0.6)
                human.setExtraKnockback(false)
            }
        }
        val velocityPacket = EntityVelocityPacket(
            targetId, toVelocityUnits(motX),
            toVelocityUnits(motY), toVelocityUnits(motZ)
        )

        if (entity is PlayerImpl) entity.connection.queuePacket(statusPacket, velocityPacket)

        if (this is PlayerImpl) player.getConnection().queuePacket(
            statusPacket, SoundEffectPacket(
                "game.player.hurt",
                toSoundUnits(entity.getX()), toSoundUnits(entity.getY()),
                toSoundUnits(entity.getZ()), 1.0f, toPitchUnits(
                    ((random.nextFloat()
                            - random.nextFloat()) * 0.2f + 1.0f).toDouble()
                )
            )
        )
        entity.setMotX(motX)
        entity.setMotY(motY)
        entity.setMotZ(motZ)
    }
}
