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
class CreativeInventoryActionPacket : Packet.INCOMING {
    private var slot: Short = 0
    private var clickedItem: ItemStack? = null

    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        slot = `is`.readShort()
        clickedItem = readSlot(`is`)
    }
}
