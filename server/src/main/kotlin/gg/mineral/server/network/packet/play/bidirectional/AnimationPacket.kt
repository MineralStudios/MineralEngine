package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class AnimationPacket(var entityId: Int = 0, var animationId: Short = 0) : Packet.Incoming, Packet.SyncHandler,
    Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeVarInt(entityId)
        os.writeByte(animationId.toInt())
    }

    override suspend fun receivedSync(connection: Connection) {
        if (animationId.toInt() == 1)
            connection.player?.swingArm()
    }

    override fun deserialize(`is`: ByteBuf) {
        entityId = `is`.readInt()
        animationId = `is`.readUnsignedByte()
    }

    override val id: Byte
        get() = 0x0B
}
