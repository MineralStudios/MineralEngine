package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class UpdateBlockEntityPacket implements Packet.OUTGOING {
    int x, z;
    short y, action;
    byte[] nbtData;

    public UpdateBlockEntityPacket(int x, short y, int z, short action, byte[] nbtData) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.action = action;
        this.nbtData = nbtData;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(x);
        os.writeShort(y);
        os.writeInt(z);
        os.writeByte(action);
        os.writeShort(nbtData.length);
        os.writeBytes(nbtData);
    }

    @Override
    public byte getId() {
        return 0x35;
    }

}
