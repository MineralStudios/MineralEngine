package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class PlayerLookPacket(var yaw: Float = 0f, var pitch: Float = 0f, var onGround: Boolean = false) : Packet.INCOMING {

    override fun received(connection: Connection) {
        val player = connection.player ?: return
        player.yaw = yaw
        player.pitch = pitch
        player.onGround = onGround
    }

    override fun deserialize(`is`: ByteBuf) {
        yaw = `is`.readFloat()
        pitch = `is`.readFloat()
        onGround = `is`.readBoolean()
    }
}
