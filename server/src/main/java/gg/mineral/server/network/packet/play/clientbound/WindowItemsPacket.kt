package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class WindowItemsPacket(val windowId: Short, val itemstacks: Array<ItemStack>) : Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        os.writeByte(windowId.toInt())
        os.writeShort(itemstacks.size)

        for (itemstack in itemstacks) writeSlot(os, itemstack)
    }

    override fun getId(): Byte {
        return 0x30
    }
}
