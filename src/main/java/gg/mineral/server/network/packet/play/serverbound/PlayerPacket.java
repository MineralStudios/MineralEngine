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
public class PlayerPacket implements Packet.INCOMING {
    private boolean onGround;

    @Override
    public void received(Connection connection) {
        val player = connection.getServer().getEntityManager().get(connection);

        if (player != null)
            player.setOnGround(onGround);
    }

    @Override
    public void deserialize(ByteBuf is) {
        onGround = is.readBoolean();
    }
}
