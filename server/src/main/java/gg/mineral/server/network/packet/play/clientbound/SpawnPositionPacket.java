package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public record SpawnPositionPacket(int x, int headY, int z) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(x);
        os.writeInt(headY);
        os.writeInt(z);
    }

    @Override
    public byte getId() {
        return 0x05;
    }
}
