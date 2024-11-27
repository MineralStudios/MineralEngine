package gg.mineral.server.network.packet.login.serverbound;

import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;
import gg.mineral.server.network.connection.ConnectionImpl;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
public final class LoginStartPacket implements Packet.INCOMING {
    private String name;

    @Override
    public void received(Connection connection) {
        if (connection instanceof ConnectionImpl impl)
            impl.attemptLogin(name);
    }

    @Override
    public void deserialize(ByteBuf is) {
        this.name = readString(is);
    }
}
