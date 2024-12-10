package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class UpdateScorePacket(val itemName: String, val scoreName: String, val updateOrRemove: Byte, val value: Int) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        writeString(os, itemName)
        os.writeByte(updateOrRemove.toInt())
        writeString(os, scoreName)
        os.writeInt(value)
    }

    override fun getId(): Byte {
        return 0x3C
    }
}
