package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class WindowItemsPacket(val windowId: UByte, val itemstacks: Array<ItemStack>) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeByte(windowId.toInt())
        os.writeShort(itemstacks.size)

        for (itemstack in itemstacks) os.writeSlot(itemstack)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WindowItemsPacket

        if (windowId != other.windowId) return false
        if (!itemstacks.contentEquals(other.itemstacks)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = windowId.toInt()
        result = 31 * result + itemstacks.contentHashCode()
        return result
    }

    override val id: Byte
        get() = 0x30
}
