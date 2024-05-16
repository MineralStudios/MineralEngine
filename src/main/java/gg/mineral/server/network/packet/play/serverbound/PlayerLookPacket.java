package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class PlayerLookPacket implements Packet.INCOMING {

    float yaw, pitch;
    boolean onGround;

    @Override
    public void received(Connection connection) {
        EntityManager.get(connection).ifPresent(player -> {
            player.setYaw(yaw);
            player.setPitch(pitch);
            player.setOnGround(onGround);
        });
    }

    @Override
    public void deserialize(ByteBuf is) {
        yaw = is.readFloat();
        pitch = is.readFloat();
        onGround = is.readBoolean();
    }
}
