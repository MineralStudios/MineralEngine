package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record UpdateScorePacket(String itemName, String scoreName, byte updateOrRemove, int value)
        implements Packet.OUTGOING {

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, itemName);
        os.writeByte(updateOrRemove);
        ByteBufUtil.writeString(os, scoreName);
        os.writeInt(value);
    }

    @Override
    public byte getId() {
        return 0x3C;
    }
}
