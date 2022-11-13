package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class TeamsPacket implements Packet.OUTGOING {
    String teamName, teamDisplayName, teamPrefix, teamSuffix;
    byte mode, friendlyFire;
    List<String> players;

    public TeamsPacket(String teamName, String teamDisplayName, String teamPrefix, String teamSuffix, byte mode,
            byte friendlyFire, List<String> players) {
        this.teamName = teamName;
        this.teamDisplayName = teamDisplayName;
        this.teamPrefix = teamPrefix;
        this.teamSuffix = teamSuffix;
        this.friendlyFire = friendlyFire;
        this.players = players;
        this.mode = mode;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, teamName);
        os.writeByte(mode);
        ByteBufUtil.writeString(os, teamDisplayName);
        ByteBufUtil.writeString(os, teamPrefix);
        ByteBufUtil.writeString(os, teamSuffix);
        os.writeByte(friendlyFire);
        os.writeShort(players.size());

        for (String player : players) {
            ByteBufUtil.writeString(os, player);
        }
    }

    @Override
    public int getId() {
        return 0x3E;
    }

}
