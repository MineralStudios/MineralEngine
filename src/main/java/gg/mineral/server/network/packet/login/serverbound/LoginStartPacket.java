package gg.mineral.server.network.packet.login.serverbound;

import gg.mineral.server.entity.PlayerManager;
import gg.mineral.server.network.Connection;
import gg.mineral.server.network.packet.IncomingPacket;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class LoginStartPacket extends IncomingPacket {

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
