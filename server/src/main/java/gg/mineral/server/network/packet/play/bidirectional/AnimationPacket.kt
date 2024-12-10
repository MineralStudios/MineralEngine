package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import lombok.experimental.Accessors

@NoArgsConstructor
@AllArgsConstructor
@Accessors(fluent = true)
class AnimationPacket : Packet.INCOMING, Packet.OUTGOING {
    private var entityId = 0
    private var animationId: Short = 0

    override fun serialize(os: ByteBuf) {
        writeVarInt(os, entityId)
        os.writeByte(animationId.toInt())
    }

    override fun received(connection: Connection) {
        if (animationId.toInt() == 1) {
            val player = connection.player

            player?.swingArm()
        }
    }

    override fun deserialize(`is`: ByteBuf) {
        entityId = `is`.readInt()
        animationId = `is`.readUnsignedByte()
    }

    override fun getId(): Byte {
        return 0x0B
    }
}
