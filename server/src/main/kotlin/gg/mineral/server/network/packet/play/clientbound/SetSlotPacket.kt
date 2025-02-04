package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class SetSlotPacket(val windowId: Byte, val slot: Short, val itemStack: ItemStack) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeByte(windowId.toInt())
        os.writeShort(slot.toInt())
        os.writeSlot(itemStack)
    }

    override val id: Byte
        get() = 0x2F
}
