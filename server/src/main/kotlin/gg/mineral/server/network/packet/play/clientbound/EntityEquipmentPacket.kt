package gg.mineral.server.network.packet.play.clientbound

import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

@JvmRecord
data class EntityEquipmentPacket(val entityId: Int, val slot: Short, val itemStack: ItemStack) : Packet.Outgoing {
    override fun serialize(os: ByteBuf) {
        os.writeInt(entityId)
        os.writeShort(slot.toInt())
        os.writeSlot(itemStack)
    }

    override val id: Byte
        get() = 0x04
}
