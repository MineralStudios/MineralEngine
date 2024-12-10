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
class KeepAlivePacket : Packet.ASYNC_INCOMING, Packet.OUTGOING {
    private var keepAliveId = 0

    override fun serialize(os: ByteBuf) {
        os.writeInt(keepAliveId)
    }

    override fun received(connection: Connection) {
    }

    override fun deserialize(`is`: ByteBuf) {
        keepAliveId = `is`.readInt()
    }

    override fun getId(): Byte {
        return 0x00
    }

    override fun receivedAsync(connection: Connection) {
    }
}
