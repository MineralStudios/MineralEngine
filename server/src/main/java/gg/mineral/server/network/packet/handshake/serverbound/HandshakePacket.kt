package gg.mineral.server.network.packet.handshake.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.network.connection.ConnectionImpl
import gg.mineral.server.network.protocol.ProtocolState
import io.netty.buffer.ByteBuf
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import lombok.experimental.Accessors

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
class HandshakePacket : Packet.INCOMING {
    private var protocol = 0
    private var port = 0
    private var nextState = 0
    private var serverAddress: String? = null

    override fun received(connection: Connection) {
        if (connection is ConnectionImpl) {
            connection.setProtocolVersion(protocol.toByte())
            connection.setProtocolState(ProtocolState.getState(nextState))
        }
    }

    override fun deserialize(`is`: ByteBuf) {
        protocol = readVarInt(`is`)
        serverAddress = readString(`is`)
        port = `is`.readUnsignedShort()
        nextState = readVarInt(`is`)
    }
}
