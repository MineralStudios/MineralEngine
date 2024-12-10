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
class UseEntityPacket : Packet.INCOMING {
    private var target = 0
    private var mouse: Byte = 0

    override fun received(connection: Connection) {
        if (mouse.toInt() == 1) { // left click
            if (target != -1) {
                val attacker: Any? = connection.player

                attacker?.attack(target)
            }
        }
    }

    override fun deserialize(`is`: ByteBuf) {
        target = `is`.readInt()
        mouse = `is`.readByte()
    }
}
