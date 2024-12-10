package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import lombok.experimental.Accessors

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
class PlayerBlockPlacementPacket : Packet.INCOMING {
    private var x = 0
    private var y: Short = 0
    private var z = 0
    private var direction: Byte = 0
    private var itemStack: ItemStack? = null
    private var cursorX: Byte = 0
    private var cursorY: Byte = 0
    private var cursorZ: Byte = 0

    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        x = `is`.readInt()
        y = `is`.readUnsignedByte()
        z = `is`.readInt()
        direction = `is`.readByte()
        itemStack = readSlot(`is`)
        cursorX = `is`.readByte()
        cursorY = `is`.readByte()
        cursorZ = `is`.readByte()
    }
}
