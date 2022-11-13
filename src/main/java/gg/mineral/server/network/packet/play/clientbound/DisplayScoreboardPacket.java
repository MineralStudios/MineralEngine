package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class DisplayScoreboardPacket implements Packet.OUTGOING {
    byte position;
    String scoreName;

    public DisplayScoreboardPacket(byte position, String scoreName) {
        this.position = position;
        this.scoreName = scoreName;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(position);
        ByteBufUtil.writeString(os, scoreName);
    }

    @Override
    public int getId() {
        return 0x3D;
    }

}
