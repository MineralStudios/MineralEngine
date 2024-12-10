package gg.mineral.server.network.packet.status.bidirectional

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
class PingPacket : Packet.INCOMING, Packet.OUTGOING {
    private var time: Long = 0

    override val id: Byte
        get() = 0x01

    override fun received(connection: Connection) {
        connection.queuePacket(this)
    }

    override fun deserialize(`is`: ByteBuf) {
        time = `is`.readLong()
    }

    override fun serialize(os: ByteBuf) {
        os.writeLong(time)
    }
}
