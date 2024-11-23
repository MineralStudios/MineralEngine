package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public class PlayerPositionPacket implements Packet.INCOMING {
    private double x, feetY, headY, z;
    private boolean onGround;

    @Override
    public void received(Connection connection) {
        val player = connection.getServer().getEntityManager().get(connection);

        if (player == null)
            return;

        val newMotX = x - player.getX();
        val newMotY = feetY - player.getY();
        val newMotZ = z - player.getZ();

        player.setMotX(newMotX);
        player.setMotY(newMotY);
        player.setMotZ(newMotZ);
        player.setX(x);
        player.setY(feetY);
        player.setHeadY(headY);
        player.setZ(z);
        player.setOnGround(onGround);
    }

    @Override
    public void deserialize(ByteBuf is) {
        x = is.readDouble();
        feetY = is.readDouble();
        headY = is.readDouble();
        z = is.readDouble();
        onGround = is.readBoolean();
    }

}
