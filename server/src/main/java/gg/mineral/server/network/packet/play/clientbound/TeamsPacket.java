package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;

public final record TeamsPacket(String teamName, String teamDisplayName, String teamPrefix, String teamSuffix,
        byte mode,
        byte friendlyFire, List<String> players) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeString(os, teamName);
        os.writeByte(mode);
        writeString(os, teamDisplayName, teamPrefix, teamSuffix);
        os.writeByte(friendlyFire);
        os.writeShort(players.size());
        writeString(os, players);
    }

    @Override
    public byte getId() {
        return 0x3E;
    }
}
