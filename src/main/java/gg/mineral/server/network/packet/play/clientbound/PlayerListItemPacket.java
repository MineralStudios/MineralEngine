package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record PlayerListItemPacket(String playerName, boolean online, short ping) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, playerName);
        os.writeBoolean(online);
        os.writeShort(ping);
    }

    @Override
    public byte getId() {
        return 0x38;
    }
}
