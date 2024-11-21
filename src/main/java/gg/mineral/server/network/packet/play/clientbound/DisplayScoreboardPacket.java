package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record DisplayScoreboardPacket(byte position, String scoreName) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(position);
        ByteBufUtil.writeString(os, scoreName);
    }

    @Override
    public byte getId() {
        return 0x3D;
    }
}
