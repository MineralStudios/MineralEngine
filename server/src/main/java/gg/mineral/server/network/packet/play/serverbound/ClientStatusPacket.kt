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
class ClientStatusPacket : Packet.INCOMING {
    private var actionId: Byte = 0

    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        actionId = `is`.readByte()
    }
}
