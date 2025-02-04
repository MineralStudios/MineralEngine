package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class CreativeInventoryActionPacket(var slot: Short = 0, var clickedItem: ItemStack? = null) : Packet.Incoming {
    override fun deserialize(`is`: ByteBuf) {
        slot = `is`.readShort()
        clickedItem = `is`.readSlot()
    }
}
