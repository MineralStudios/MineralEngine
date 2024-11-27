package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record EntityRelativeMovePacket(int entityId, byte deltaX, byte deltaY, byte deltaZ)
        implements Packet.OUTGOING {
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
