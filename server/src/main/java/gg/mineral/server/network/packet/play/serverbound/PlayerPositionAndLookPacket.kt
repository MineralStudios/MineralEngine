package gg.mineral.server.network.packet.play.serverbound

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
class PlayerPositionAndLookPacket : Packet.INCOMING {
    private var x = 0.0
    private var feetY = 0.0
    private var headY = 0.0
    private var z = 0.0
    private var yaw = 0f
    private var pitch = 0f
    private var onGround = false

    override fun received(connection: Connection) {
        val player: Any = connection.player!!

        val newMotX: Any = x - player.getX()
        val newMotY: Any = feetY - player.getY()
        val newMotZ: Any = z - player.getZ()

        player.setMotX(newMotX)
        player.setMotY(newMotY)
        player.setMotZ(newMotZ)
        player.setX(x)
        player.setY(feetY)
        player.setHeadY(headY)
        player.setZ(z)
        player.setYaw(yaw)
        player.setPitch(pitch)
        player.setOnGround(onGround)
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
