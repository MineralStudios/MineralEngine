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
class CloseWindowPacket : Packet.INCOMING, Packet.OUTGOING {
    private var windowId: Short = 0

    override fun serialize(os: ByteBuf) {
        os.writeByte(windowId.toInt())
    }

    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        windowId = `is`.readByte().toShort()
    }

    override fun getId(): Byte {
        return 0x2E
    }
}
