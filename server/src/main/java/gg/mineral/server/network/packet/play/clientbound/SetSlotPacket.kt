package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SetSlotPacket(val windowId: Byte, val slot: Short, val itemStack: ItemStack) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeByte(windowId.toInt())
        os.writeShort(slot.toInt())
        writeSlot(os, itemStack)
    }

    override fun getId(): Byte {
        return 0x2F
    }
}
