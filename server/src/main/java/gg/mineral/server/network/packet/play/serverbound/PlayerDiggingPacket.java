package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.entity.living.human.property.Gamemode;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.network.packet.play.clientbound.BlockChangePacket;
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
public class PlayerDiggingPacket implements Packet.INCOMING {
    private byte status, face;
    private int x;
    private short y;
    private int z;

    @Override
    public void received(Connection connection) {
        val player = connection.getServer().getEntityManager().get(connection);

        if (player == null)
            return;

        if (status == 2 || (status == 0 && player.getGamemode() == Gamemode.CREATIVE)) { // done digging
            val world = player.getWorld();
            int type = world.getType(x, y, z);
            int meta = world.getMetaData(x, y, z);
            connection.queuePacket(new BlockChangePacket(x, y, z, type, (short) meta));
        }
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
