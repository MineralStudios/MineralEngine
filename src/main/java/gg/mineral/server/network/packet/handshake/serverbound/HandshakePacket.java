package gg.mineral.server.network.packet.handshake.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.network.protocol.ProtocolState;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class HandshakePacket implements Packet.INCOMING {

    int protocol, port, nextState;
    String serverAddress;

    @Override
    public void received(Connection connection) {
        connection.PROTOCOL_VERSION = protocol;
        connection.setProtocolState(ProtocolState.getState(nextState));
    }

    @Override
    public void deserialize(ByteBuf is) {
        protocol = ByteBufUtil.readVarInt(is);
        serverAddress = ByteBufUtil.readString(is);
        port = is.readUnsignedShort();
        nextState = ByteBufUtil.readVarInt(is);
    }

}
