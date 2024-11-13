package gg.mineral.server.network.packet.handshake.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.network.protocol.ProtocolState;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandshakePacket implements Packet.INCOMING {
    private int protocol, port, nextState;
    private String serverAddress;

    @Override
    public void received(Connection connection) {
        connection.PROTOCOL_VERSION = (byte) protocol;
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
