package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.math.MathUtil;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class SpawnGlobalEntityPacket implements Packet.OUTGOING {
    int entityId, x, y, z;
    byte type;

    public SpawnGlobalEntityPacket(int entityId, double x, double y, double z, byte type) {
        this.entityId = entityId;
        this.x = MathUtil.toFixedPointInt(x);
        this.y = MathUtil.toFixedPointInt(y);
        this.z = MathUtil.toFixedPointInt(z);
        this.type = type;
    }

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
