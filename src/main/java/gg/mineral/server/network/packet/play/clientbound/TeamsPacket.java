package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.val;

public record TeamsPacket(String teamName, String teamDisplayName, String teamPrefix, String teamSuffix, byte mode,
        byte friendlyFire, List<String> players) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, teamName);
        os.writeByte(mode);
        ByteBufUtil.writeString(os, teamDisplayName);
        ByteBufUtil.writeString(os, teamPrefix);
        ByteBufUtil.writeString(os, teamSuffix);
        os.writeByte(friendlyFire);
        os.writeShort(players.size());

        for (val player : players)
            ByteBufUtil.writeString(os, player);
    }

    @Override
    public byte getId() {
        return 0x3E;
    }
}
