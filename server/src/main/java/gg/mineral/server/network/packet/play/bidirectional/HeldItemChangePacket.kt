package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import lombok.experimental.Accessors

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(fluent = true)
class HeldItemChangePacket : Packet.INCOMING, Packet.OUTGOING {
    private var slot: Short = 0

    override fun serialize(os: ByteBuf) {
        os.writeByte(slot.toInt())
    }

    override fun getId(): Byte {
        return 0x09
    }

    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        slot = `is`.readShort()
    }
}
