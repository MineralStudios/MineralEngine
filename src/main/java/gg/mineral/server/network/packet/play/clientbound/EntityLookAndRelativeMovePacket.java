package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.math.MathUtil;
import io.netty.buffer.ByteBuf;

public class EntityLookAndRelativeMovePacket implements Packet.OUTGOING {

    int entityId;
    byte deltaX, deltaY, deltaZ, yaw, pitch;

    public EntityLookAndRelativeMovePacket(int entityId, double deltaX, double deltaY, double deltaZ, byte yaw,
            byte pitch) {
        this.entityId = entityId;
        this.deltaX = MathUtil.toFixedPointByte(deltaX);
        this.deltaY = MathUtil.toFixedPointByte(deltaY);
        this.deltaZ = MathUtil.toFixedPointByte(deltaZ);
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeByte(deltaX);
        os.writeByte(deltaY);
        os.writeByte(deltaZ);
        os.writeByte(yaw);
        os.writeByte(pitch);
    }

    @Override
    public int getId() {
        return 0x17;
    }

}
