package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class BlockBreakAnimationPacket implements Packet.OUTGOING {
    int entityId, x, y, z;
    byte destroyStage;

    public BlockBreakAnimationPacket(int entityId, int x, int y, int z, byte destroyStage) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.destroyStage = destroyStage;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, entityId);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeByte(destroyStage);
    }

    @Override
    public byte getId() {
        return 0x25;
    }

}
