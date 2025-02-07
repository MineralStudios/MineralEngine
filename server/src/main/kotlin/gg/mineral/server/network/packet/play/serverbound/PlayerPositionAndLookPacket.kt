package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import io.netty.buffer.ByteBuf

class PlayerPositionAndLookPacket(
    var x: Double = 0.0,
    var feetY: Double = 0.0,
    var headY: Double = 0.0,
    var z: Double = 0.0,
    var yaw: Float = 0f,
    var pitch: Float = 0f,
    onGround: Boolean = false
) : PlayerPacket(onGround) {

    override fun receivedSync(connection: Connection) {
        connection.player?.let {
            it.motX = x - it.x
            it.motY = feetY - it.y
            it.motZ = z - it.z
            it.x = x
            it.y = feetY
            it.headY = headY
            it.z = z
            it.yaw = yaw
            it.pitch = pitch
        }

        super.receivedSync(connection)
    }

    override fun deserialize(`is`: ByteBuf) {
        x = `is`.readDouble()
        feetY = `is`.readDouble()
        headY = `is`.readDouble()
        z = `is`.readDouble()
        yaw = `is`.readFloat()
        pitch = `is`.readFloat()
        super.deserialize(`is`)
    }
}
