package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.entity.living.human.property.PlayerAbilities
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class PlayerAbilitiesPacket(playerAbilities: PlayerAbilities? = null) : Packet.INCOMING,
    Packet.OUTGOING {
    private var flags: Byte = 0
    private var flyingSpeed: Float = 0f
    private var walkingSpeed: Float = 0f

    init {
        if (playerAbilities != null) {
            if (playerAbilities.isInvulnerable) flags = (flags.toInt() or 0x1).toByte()
            if (playerAbilities.flying) flags = (flags.toInt() or 0x2).toByte()
            if (playerAbilities.canFly) flags = (flags.toInt() or 0x4).toByte()
            if (playerAbilities.canInstantlyBuild) flags = (flags.toInt() or 0x8).toByte()

            this.flyingSpeed = playerAbilities.flySpeed
            this.walkingSpeed = playerAbilities.walkSpeed
        }
    }

    override fun serialize(os: ByteBuf) {
        os.writeByte(flags.toInt())
        os.writeFloat(flyingSpeed)
        os.writeFloat(walkingSpeed)
    }

    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        flags = `is`.readByte()
        flyingSpeed = `is`.readFloat()
        walkingSpeed = `is`.readFloat()
    }

    override val id: Byte
        get() = 0x39
}
