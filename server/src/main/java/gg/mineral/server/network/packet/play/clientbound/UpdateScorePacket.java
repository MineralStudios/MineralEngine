package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record UpdateScorePacket(String itemName, String scoreName, byte updateOrRemove, int value)
        implements Packet.OUTGOING {

    @Override
    public void serialize(ByteBuf os) {
        writeString(os, itemName);
        os.writeByte(updateOrRemove);
        writeString(os, scoreName);
        os.writeInt(value);
    }

    @Override
    public byte getId() {
        return 0x3C;
    }
}
