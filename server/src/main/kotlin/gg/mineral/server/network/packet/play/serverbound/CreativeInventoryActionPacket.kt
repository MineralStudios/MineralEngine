package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class CreativeInventoryActionPacket(var slot: Short = 0, var clickedItem: ItemStack? = null) : Packet.INCOMING {
    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        slot = `is`.readShort()
        clickedItem = readSlot(`is`)
    }
}
