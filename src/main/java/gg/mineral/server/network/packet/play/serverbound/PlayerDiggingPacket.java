package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.entity.living.human.property.Gamemode;
import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.network.packet.play.clientbound.BlockChangePacket;
import io.netty.buffer.ByteBuf;

public class PlayerDiggingPacket implements Packet.INCOMING {
    byte status, face;
    int x, z;
    short y;

    @Override
    public void received(Connection connection) {
        EntityManager.get(p -> p.getConnection().equals(connection)).ifPresent(player -> {
            if (status == 2 || (status == 0 && player.getGamemode() == Gamemode.CREATIVE)) { // done digging
                int type = player.getWorld().getType(x, y, z);
                int meta = player.getWorld().getMetaData(x, y, z);
                connection.queuePacket(new BlockChangePacket(x, y, z, type, (short) meta));
            }
        });
    }

    @Override
    public void deserialize(ByteBuf is) {
        status = is.readByte();
        x = is.readInt();
        y = is.readUnsignedByte();
        z = is.readInt();
        face = is.readByte();
    }

}
