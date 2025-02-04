package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.entity.living.human.property.PlayerAbilities
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class PlayerAbilitiesPacket(playerAbilities: PlayerAbilities? = null) : Packet.Incoming,
    Packet.Outgoing {
    private var flags: Byte = 0
    private var flyingSpeed: Float = 0f
    private var walkingSpeed: Float = 0f

    init {
        playerAbilities?.let {
            if (it.isInvulnerable) flags = (flags.toInt() or 0x1).toByte()
            if (it.flying) flags = (flags.toInt() or 0x2).toByte()
            if (it.canFly) flags = (flags.toInt() or 0x4).toByte()
            if (it.canInstantlyBuild) flags = (flags.toInt() or 0x8).toByte()

            this.flyingSpeed = it.flySpeed
            this.walkingSpeed = it.walkSpeed
        }
    }

    override fun serialize(os: ByteBuf) {
        os.writeByte(flags.toInt())
        os.writeFloat(flyingSpeed, walkingSpeed)
    }

    override fun deserialize(`is`: ByteBuf) {
        flags = `is`.readByte()
        flyingSpeed = `is`.readFloat()
        walkingSpeed = `is`.readFloat()
    }

    override val id: Byte
        get() = 0x39
}
