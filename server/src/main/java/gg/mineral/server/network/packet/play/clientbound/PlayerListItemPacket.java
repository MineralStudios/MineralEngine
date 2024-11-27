package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;

public final record PlayerListItemPacket(String playerName, boolean online, short ping) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeString(os, playerName);
        os.writeBoolean(online);
        os.writeShort(ping);
    }

    @Override
    public byte getId() {
        return 0x38;
    }
}
