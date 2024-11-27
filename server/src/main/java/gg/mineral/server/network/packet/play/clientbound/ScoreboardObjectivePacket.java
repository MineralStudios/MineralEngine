package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;

public final record ScoreboardObjectivePacket(String objectiveName, String objectiveValue, byte createOrRemove)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeString(os, objectiveName);
        writeString(os, objectiveValue);
        os.writeByte(createOrRemove);
    }

    @Override
    public byte getId() {
        return 0x3B;
    }
}
