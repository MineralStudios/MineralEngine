package gg.mineral.server.network.packet.handshake.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.network.connection.ConnectionImpl
import gg.mineral.server.network.protocol.ProtocolState
import io.netty.buffer.ByteBuf

class HandshakePacket(
    var protocol: Int = 0,
    var port: Int = 0,
    var nextState: Int = 0,
    var serverAddress: String? = null
) : Packet.INCOMING {
    override fun received(connection: Connection) {
        if (connection is ConnectionImpl) {
            connection.protocolVersion = protocol.toByte()
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
