package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class OpenWindowPacket(
    val windowId: UByte, val inventoryType: UByte, val windowTitle: String, val numberOfSlots: UByte,
    val useProvidedWindowTitle: Boolean, val entityId: Int
) : Packet.Outgoing {
    @OptIn(ExperimentalUnsignedTypes::class)
    override fun serialize(os: ByteBuf) {
        os.writeByte(windowId, inventoryType)
        os.writeString(windowTitle)
        os.writeByte(numberOfSlots)
        os.writeBoolean(useProvidedWindowTitle)
        os.writeInt(entityId)
    }

    override val id: Byte
        get() = 0x2D
}
