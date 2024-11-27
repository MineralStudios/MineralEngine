package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record DisplayScoreboardPacket(byte position, String scoreName) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(position);
        writeString(os, scoreName);
    }

    @Override
    public byte getId() {
        return 0x3D;
    }
}
