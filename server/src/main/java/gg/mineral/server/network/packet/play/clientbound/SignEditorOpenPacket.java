package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record SignEditorOpenPacket(int x, int y, int z) implements Packet.OUTGOING {
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
