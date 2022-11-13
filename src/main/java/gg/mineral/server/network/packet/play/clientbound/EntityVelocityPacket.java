package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.math.MathUtil;
import io.netty.buffer.ByteBuf;

public class EntityVelocityPacket implements Packet.OUTGOING {
    int entityId;
    short x, y, z;

    public EntityVelocityPacket(int entityId, double x, double y, double z) {
        this.entityId = entityId;
        this.x = MathUtil.toVelocityUnits(x);
        this.y = MathUtil.toVelocityUnits(y);
        this.z = MathUtil.toVelocityUnits(z);
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeShort(x);
        os.writeShort(y);
        os.writeShort(z);
    }

    @Override
    public int getId() {
        return 0x12;
    }

}
