package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record ScoreboardObjectivePacket(String objectiveName, String objectiveValue, byte createOrRemove)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, objectiveName);
        ByteBufUtil.writeString(os, objectiveValue);
        os.writeByte(createOrRemove);
    }

    @Override
    public byte getId() {
        return 0x3B;
    }
}
