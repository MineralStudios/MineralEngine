package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class ClickWindowPacket(
    var windowId: Byte = 0,
    var slot: Short = 0,
    var button: Byte = 0,
    var actionNumber: Short = 0,
    var mode: Byte = 0,
    var clickedItem: ItemStack? = null
) : Packet.INCOMING {
    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        windowId = `is`.readByte()
        slot = `is`.readShort()
        button = `is`.readByte()
        actionNumber = `is`.readShort()
        mode = `is`.readByte()
        clickedItem = readSlot(`is`)
    }
}
