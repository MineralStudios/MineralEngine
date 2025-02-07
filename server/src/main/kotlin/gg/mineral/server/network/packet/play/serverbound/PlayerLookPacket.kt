package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import io.netty.buffer.ByteBuf

class PlayerLookPacket(var yaw: Float = 0f, var pitch: Float = 0f, onGround: Boolean = false) : PlayerPacket(onGround) {

    override fun receivedSync(connection: Connection) {
        connection.player?.let {
            it.yaw = yaw
            it.pitch = pitch
        }
        super.receivedSync(connection)
    }

    override fun deserialize(`is`: ByteBuf) {
        yaw = `is`.readFloat()
        pitch = `is`.readFloat()
        super.deserialize(`is`)
    }
}
