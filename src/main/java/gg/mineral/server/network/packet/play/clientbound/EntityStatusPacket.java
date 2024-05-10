package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class EntityStatusPacket implements Packet.OUTGOING {

    int entityId;
    byte entityStatus;

    public EntityStatusPacket(int entityId, byte entityStatus) {
        this.entityId = entityId;
        this.entityStatus = entityStatus;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeByte(entityStatus);
    }

    @Override
    public byte getId() {
        return 0x1A;
    }

}
