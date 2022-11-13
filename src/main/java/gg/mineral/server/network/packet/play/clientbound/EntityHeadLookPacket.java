package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class EntityHeadLookPacket implements Packet.OUTGOING {

    int entityId;
    byte headYaw;

    public EntityHeadLookPacket(int entityId, byte headYaw) {
        this.entityId = entityId;
        this.headYaw = headYaw;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeByte(headYaw);
    }

    @Override
    public int getId() {
        return 0x19;
    }

}
