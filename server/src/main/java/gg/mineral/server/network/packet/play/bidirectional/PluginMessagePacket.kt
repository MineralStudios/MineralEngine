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
class PluginMessagePacket : Packet.INCOMING, Packet.OUTGOING {
    private var channel: String? = null
    private var data: ByteArray

    override fun serialize(os: ByteBuf) {
        writeString(os, channel)
        os.writeShort(data.size)
        os.writeBytes(data)
    }

    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        channel = readString(`is`)
        val length = `is`.readShort()
        data = ByteArray(length.toInt())
        `is`.readBytes(data)
    }

    override fun getId(): Byte {
        return 0x3F
    }
}
