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
) : Packet.Incoming, Packet.EventLoopHandler {
    override fun receivedEventLoop(connection: Connection) {
        if (connection is ConnectionImpl) {
            connection.protocolVersion = protocol.toByte()
            connection.protocolState = ProtocolState.getState(nextState)
        }
    }

    override fun deserialize(`is`: ByteBuf) {
        protocol = `is`.readVarInt()
        serverAddress = `is`.readString()
        port = `is`.readUnsignedShort()
        nextState = `is`.readVarInt()
    }
}
