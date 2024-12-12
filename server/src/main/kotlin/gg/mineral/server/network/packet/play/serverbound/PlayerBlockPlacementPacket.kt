package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class PlayerBlockPlacementPacket(
    var x: Int = 0,
    var y: Short = 0,
    var z: Int = 0,
    var direction: Byte = 0,
    var itemStack: ItemStack? = null,
    var cursorX: Byte = 0,
    var cursorY: Byte = 0,
    var cursorZ: Byte = 0
) : Packet.INCOMING {
    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        x = `is`.readInt()
        y = `is`.readUnsignedByte()
        z = `is`.readInt()
        direction = `is`.readByte()
        itemStack = `is`.readSlot()
        cursorX = `is`.readByte()
        cursorY = `is`.readByte()
        cursorZ = `is`.readByte()
    }
}
