package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class PlayerPacket implements Packet.INCOMING {

    boolean onGround;

    @Override
    public void received(Connection connection) {
        EntityManager.get(connection).ifPresent(player -> player.setOnGround(onGround));
    }

    @Override
    public void deserialize(ByteBuf is) {
        onGround = is.readBoolean();
    }
}
