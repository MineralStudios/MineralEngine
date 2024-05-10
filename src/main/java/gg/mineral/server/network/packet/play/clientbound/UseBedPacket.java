package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class UseBedPacket implements Packet.OUTGOING {
    int entityId, x, z;
    short y;

    public UseBedPacket(int entityId, int x, short y, int z) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeInt(x);
        os.writeByte(y);
        os.writeInt(z);
    }

    @Override
    public byte getId() {
        return 0x0A;
    }

}
