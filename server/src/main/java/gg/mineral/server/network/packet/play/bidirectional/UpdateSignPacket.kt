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
class UpdateSignPacket : Packet.INCOMING, Packet.OUTGOING {
    private var x = 0
    private var y: Short = 0
    private var z = 0
    private var line1: String? = null
    private var line2: String? = null
    private var line3: String? = null
    private var line4: String? = null

    override fun serialize(os: ByteBuf) {
        os.writeInt(x)
        os.writeShort(y.toInt())
        os.writeInt(z)
        writeString(os, line1, line2, line3, line4)
    }

    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        x = `is`.readInt()
        y = `is`.readShort()
        z = `is`.readInt()
        line1 = readString(`is`)
        line2 = readString(`is`)
        line3 = readString(`is`)
        line4 = readString(`is`)
    }

    override fun getId(): Byte {
        return 0x33
    }
}
