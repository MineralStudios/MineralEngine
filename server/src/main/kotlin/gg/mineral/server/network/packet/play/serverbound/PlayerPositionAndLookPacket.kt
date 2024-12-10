package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf

class PlayerPositionAndLookPacket(
    var x: Double = 0.0,
    var feetY: Double = 0.0,
    var headY: Double = 0.0,
    var z: Double = 0.0,
    var yaw: Float = 0f,
    var pitch: Float = 0f,
    var onGround: Boolean = false
) : Packet.INCOMING {

    override fun received(connection: Connection) {
        val player: Player = connection.player ?: return

        val newMotX = x - player.x
        val newMotY = feetY - player.y
        val newMotZ = z - player.z

        player.motX = newMotX
        player.motY = newMotY
        player.motZ = newMotZ
        player.x = x
        player.y = feetY
        player.headY = headY
        player.z = z
        player.yaw = yaw
        player.pitch = pitch
        player.onGround = onGround
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
