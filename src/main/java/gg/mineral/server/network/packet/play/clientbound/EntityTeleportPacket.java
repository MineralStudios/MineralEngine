package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.math.MathUtil;
import io.netty.buffer.ByteBuf;

public class EntityTeleportPacket implements Packet.OUTGOING {
    int entityId, x, y, z;
    byte yaw, pitch;

    public EntityTeleportPacket(int entityId, double x, double y, double z, byte yaw, byte pitch) {
        this.entityId = entityId;
        this.x = MathUtil.toFixedPointInt(x);
        this.y = MathUtil.toFixedPointInt(y);
        this.z = MathUtil.toFixedPointInt(z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeByte(yaw);
        os.writeByte(pitch);
    }

    @Override
    public int getId() {
        return 0x18;
    }

}
