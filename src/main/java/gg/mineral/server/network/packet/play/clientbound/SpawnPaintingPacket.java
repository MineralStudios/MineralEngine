package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class SpawnPaintingPacket implements Packet.OUTGOING {
    int entityId, x, y, z, direction;
    String title;

    public SpawnPaintingPacket(int entityId, int x, int y, int z, int direction, String title) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = direction;
        this.title = title;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, entityId);
        ByteBufUtil.writeString(os, title);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeInt(direction);
    }

    @Override
    public byte getId() {
        return 0x10;
    }

}
