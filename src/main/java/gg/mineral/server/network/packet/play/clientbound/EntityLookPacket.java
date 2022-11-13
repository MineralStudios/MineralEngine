package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class EntityLookPacket implements Packet.OUTGOING {

    int entityId;
    byte yaw, pitch;

    public EntityLookPacket(int entityId, byte yaw, byte pitch) {
        this.entityId = entityId;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeByte(yaw);
        os.writeByte(pitch);
    }

    @Override
    public int getId() {
        return 0x16;
    }

}
