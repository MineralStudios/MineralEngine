package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class SignEditorOpenPacket implements Packet.OUTGOING {
    int x, y, z;

    public SignEditorOpenPacket(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;

    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
    }

    @Override
    public byte getId() {
        return 0x36;
    }

}
