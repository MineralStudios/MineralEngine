package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class EntityRelativeMovePacket implements Packet.OUTGOING {

    int entityId;
    byte deltaX, deltaY, deltaZ;

    public EntityRelativeMovePacket(int entityId, byte deltaX, byte deltaY, byte deltaZ) {
        this.entityId = entityId;
        this.deltaX = deltaX;
        this.deltaY = deltaY;
        this.deltaZ = deltaZ;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeByte(deltaX);
        os.writeByte(deltaY);
        os.writeByte(deltaZ);
    }

    @Override
    public byte getId() {
        return 0x15;
    }

}
