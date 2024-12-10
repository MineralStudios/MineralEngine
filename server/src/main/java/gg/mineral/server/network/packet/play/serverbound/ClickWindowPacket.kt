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
class ClickWindowPacket : Packet.INCOMING {
    private var windowId: Byte = 0
    private var slot: Short = 0
    private var button: Byte = 0
    private var actionNumber: Short = 0
    private var mode: Byte = 0
    private var clickedItem: ItemStack? = null

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
