package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class EntityActionPacket implements Packet.INCOMING {
    int entityId, jumpBoost;
    byte actionId;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        entityId = is.readInt();
        actionId = is.readByte();
        jumpBoost = is.readInt();
    }

}
