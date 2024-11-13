package gg.mineral.server.network.packet.login.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginStartPacket implements Packet.INCOMING {
    private String name;

    @Override
    public void received(Connection connection) {
        connection.attemptLogin(name);
    }

    @Override
    public void deserialize(ByteBuf is) {
        this.name = ByteBufUtil.readString(is);
    }
}
