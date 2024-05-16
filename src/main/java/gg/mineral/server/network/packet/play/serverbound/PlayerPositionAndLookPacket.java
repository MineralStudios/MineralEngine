package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class PlayerPositionAndLookPacket implements Packet.INCOMING {

    double x, feetY, headY, z;
    float yaw, pitch;
    boolean onGround;

    @Override
    public void received(Connection connection) {
        EntityManager.get(connection).ifPresent(player -> {
            player.setX(x);
            player.setY(feetY);
            player.setHeadY(headY);
            player.setZ(z);
            player.setMotX(x - player.getX());
            player.setMotY(feetY - player.getY());
            player.setMotZ(z - player.getZ());
            player.setYaw(yaw);
            player.setPitch(pitch);
            player.setOnGround(onGround);
        });
    }

    @Override
    public void deserialize(ByteBuf is) {
        x = is.readDouble();
        feetY = is.readDouble();
        headY = is.readDouble();
        z = is.readDouble();
        yaw = is.readFloat();
        pitch = is.readFloat();
        onGround = is.readBoolean();
    }

}
