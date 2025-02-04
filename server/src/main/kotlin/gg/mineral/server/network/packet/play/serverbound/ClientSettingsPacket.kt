package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.api.world.property.Difficulty
import io.netty.buffer.ByteBuf

class ClientSettingsPacket(
    var locale: String? = null,
    var viewDistance: Byte = 0,
    var chatFlags: Byte = 0,
    var chatColors: Boolean = false,
    var difficulty: Difficulty? = null,
    var showCape: Boolean = false
) : Packet.Incoming, Packet.AsyncHandler {

    override fun deserialize(`is`: ByteBuf) {
        locale = `is`.readString()
        viewDistance = `is`.readByte()
        chatFlags = `is`.readByte()
        chatColors = `is`.readBoolean()
        difficulty = Difficulty.fromId(`is`.readByte())
        showCape = `is`.readBoolean()
    }

    override suspend fun receivedAsync(connection: Connection) {
        connection.player?.let {
            it.viewDistance = viewDistance.coerceAtLeast(2)
        }
    }
}
