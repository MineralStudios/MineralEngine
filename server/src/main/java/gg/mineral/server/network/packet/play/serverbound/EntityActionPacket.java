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
public class EntityActionPacket implements Packet.INCOMING {
    private int entityId;
    private byte actionId;
    private int jumpBoost;

    @Override
    public void received(Connection connection) {
        val player = connection.getServer().getEntityManager().getPlayer(entityId);

        if (player == null)
            return;
        switch (actionId) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:

                break;
            case 4:
                player.setSprinting(true);
                player.setExtraKnockback(true);
                break;
            case 5:
                player.setSprinting(false);
                player.setExtraKnockback(false);
                break;
            case 6:
                break;
        }
    }

    @Override
    public void deserialize(ByteBuf is) {
        entityId = is.readInt();
        actionId = is.readByte();
        jumpBoost = is.readInt();
    }

}
