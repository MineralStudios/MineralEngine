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
class PlayerPacket : Packet.INCOMING {
    private var onGround = false

    override fun received(connection: Connection) {
        val player: Any? = connection.player

        player?.setOnGround(onGround)
    }

    override fun deserialize(`is`: ByteBuf) {
        onGround = `is`.readBoolean()
    }
}
