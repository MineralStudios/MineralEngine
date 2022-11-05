package gg.mineral.server.network.packet.login.serverbound;

import gg.mineral.server.entity.PlayerManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class LoginStartPacket implements Packet.INCOMING {

    String name;

    @Override
    public void received(Connection connection) {
        PlayerManager.create(name, connection).login();
    }

    @Override
    public void deserialize(ByteBuf is) {
        name = ByteBufUtil.readString(is);
    }

    @Override
    public int getId() {
        return 0x00;
    }

}
