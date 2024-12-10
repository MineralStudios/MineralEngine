package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.api.world.property.Difficulty
import io.netty.buffer.ByteBuf
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import lombok.experimental.Accessors

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
class ClientSettingsPacket : Packet.INCOMING {
    private var locale: String? = null
    private var viewDistance: Byte = 0
    private var chatFlags: Byte = 0
    private var chatColors = false
    private var difficulty: Difficulty? = null
    private var showCape = false

    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        locale = readString(`is`)
        viewDistance = `is`.readByte()
        chatFlags = `is`.readByte()
        chatColors = `is`.readBoolean()
        difficulty = Difficulty.fromId(`is`.readByte())
        showCape = `is`.readBoolean()
    }
}
