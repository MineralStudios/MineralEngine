package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.val;

public class PlayerLookPacket implements Packet.INCOMING {

    float yaw, pitch;
    boolean onGround;

    @Override
    public void received(Connection connection) {
        val player = EntityManager.get(connection);

        if (player == null)
            return;

        player.setYaw(yaw);
        player.setPitch(pitch);
        player.setOnGround(onGround);
    }

    @Override
    public void deserialize(ByteBuf is) {
        yaw = is.readFloat();
        pitch = is.readFloat();
        onGround = is.readBoolean();
    }
}
