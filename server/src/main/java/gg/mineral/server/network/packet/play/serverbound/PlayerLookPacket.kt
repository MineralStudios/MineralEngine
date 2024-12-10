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
class PlayerLookPacket : Packet.INCOMING {
    private var yaw = 0f
    private var pitch = 0f
    private var onGround = false

    override fun received(connection: Connection) {
        val player = connection.player ?: return

        player.setYaw(yaw)
        player.setPitch(pitch)
        player.setOnGround(onGround)
    }

    override fun deserialize(`is`: ByteBuf) {
        yaw = `is`.readFloat()
        pitch = `is`.readFloat()
        onGround = `is`.readBoolean()
    }
}
