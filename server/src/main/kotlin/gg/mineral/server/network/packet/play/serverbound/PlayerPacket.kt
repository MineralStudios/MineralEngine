package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class PlayerPacket(var onGround: Boolean = false) : Packet.INCOMING {
    override fun received(connection: Connection) {
        connection.player?.onGround = onGround
    }

    override fun deserialize(`is`: ByteBuf) {
        onGround = `is`.readBoolean()
    }
}
