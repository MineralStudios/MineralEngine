package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class EntityActionPacket implements Packet.INCOMING {
    int entityId, jumpBoost;
    byte actionId;

    @Override
    public void received(Connection connection) {
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
                EntityManager.getPlayer(entityId)
                        .ifPresent(player -> {
                            player.setSprinting(true);
                            player.setExtraKnockback(true);
                        });
                break;
            case 5:
                EntityManager.getPlayer(entityId)
                        .ifPresent(player -> {
                            player.setSprinting(false);
                            player.setExtraKnockback(false);
                        });
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
