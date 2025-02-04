package gg.mineral.server.network.packet.play.bidirectional

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf


class ConfirmTransactionPacket(
    var windowId: UByte = 0.toUByte(),
    var actionNumber: Short = 0,
    var accepted: Boolean = false
) :
    Packet.Incoming, Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeByte(windowId.toInt())
        os.writeShort(actionNumber.toInt())
        os.writeBoolean(accepted)
    }

    override val id: Byte
        get() = 0x32

    override fun deserialize(`is`: ByteBuf) {
        windowId = `is`.readByte().toUByte()
        actionNumber = `is`.readShort()
        accepted = `is`.readBoolean()
    }
}
