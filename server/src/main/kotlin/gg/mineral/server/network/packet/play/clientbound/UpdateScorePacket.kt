package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class UpdateScorePacket(val itemName: String, val scoreName: String, val updateOrRemove: Byte, val value: Int) :
    Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeString(itemName)
        os.writeByte(updateOrRemove.toInt())
        os.writeString(scoreName)
        os.writeInt(value)
    }

    override val id: Byte
        get() = 0x3C
}
