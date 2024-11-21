package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record SpawnPaintingPacket(int entityId, String title, int x, int y, int z, int direction)
        implements Packet.OUTGOING {

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
