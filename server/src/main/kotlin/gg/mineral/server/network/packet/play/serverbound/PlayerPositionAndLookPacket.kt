package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import io.netty.buffer.ByteBuf

class PlayerPositionAndLookPacket(
    override var x: Double = 0.0,
    override var feetY: Double = 0.0,
    override var headY: Double = 0.0,
    override var z: Double = 0.0,
    var yaw: Float = 0f,
    var pitch: Float = 0f,
    onGround: Boolean = false
) : PlayerPositionPacket(x, feetY, headY, z, onGround) {

    override suspend fun receivedSync(connection: Connection) {
        super.receivedSync(connection)

        connection.player?.let {
            it.yaw = yaw
            it.pitch = pitch
        }
    }

    override fun deserialize(`is`: ByteBuf) {
        x = `is`.readDouble()
        feetY = `is`.readDouble()
        headY = `is`.readDouble()
        z = `is`.readDouble()
        yaw = `is`.readFloat()
        pitch = `is`.readFloat()
        onGround = `is`.readBoolean()
    }
}
