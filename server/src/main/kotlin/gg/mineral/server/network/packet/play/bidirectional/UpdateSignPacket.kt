package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class UpdateSignPacket(
    var x: Int = 0,
    var y: Short = 0,
    var z: Int = 0,
    var line1: String = "",
    var line2: String = "",
    var line3: String = "",
    var line4: String = ""
) : Packet.INCOMING, Packet.OUTGOING {
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

    override val id: Byte
        get() = 0x33
}
