package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class OpenWindowPacket(
    val windowId: Short, val inventoryType: Short, val windowTitle: String, val numberOfSlots: Short,
    val useProvidedWindowTitle: Boolean, val entityId: Int
) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeByte(windowId.toInt())
        os.writeByte(inventoryType.toInt())
        writeString(os, windowTitle)
        os.writeByte(numberOfSlots.toInt())
        os.writeBoolean(useProvidedWindowTitle)
        os.writeInt(entityId)
    }

    override val id: Byte
        get() = 0x2D
}
