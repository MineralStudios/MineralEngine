package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class EntityLookAndRelativeMovePacket implements Packet.OUTGOING {

    int entityId;
    byte deltaX, deltaY, deltaZ, yaw, pitch;

    public EntityLookAndRelativeMovePacket(int entityId, byte deltaX, byte deltaY, byte deltaZ, byte yaw,
            byte pitch) {
        this.entityId = entityId;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
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
    public byte getId() {
        return 0x17;
    }

}
