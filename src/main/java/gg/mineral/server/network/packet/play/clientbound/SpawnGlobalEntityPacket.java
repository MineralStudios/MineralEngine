package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record SpawnGlobalEntityPacket(int entityId, byte type, int x, int y, int z) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, entityId);
        os.writeByte(type);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
    }

    @Override
    public byte getId() {
        return 0x2C;
    }
}
