package gg.mineral.server.network.packet.handshake.serverbound;

import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;
import gg.mineral.server.network.connection.ConnectionImpl;
import gg.mineral.server.network.protocol.ProtocolState;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
public final class HandshakePacket implements Packet.INCOMING {
    private int protocol, port, nextState;
    private String serverAddress;

    @Override
    public void received(Connection connection) {
        if (connection instanceof ConnectionImpl impl) {
            impl.setProtocolVersion((byte) protocol);
            impl.setProtocolState(ProtocolState.getState(nextState));
        }
    }

    @Override
    public void deserialize(ByteBuf is) {
        protocol = readVarInt(is);
        serverAddress = readString(is);
        port = is.readUnsignedShort();
        nextState = readVarInt(is);
    }
}
