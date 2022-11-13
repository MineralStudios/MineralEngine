package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class PlayerListItemPacket implements Packet.OUTGOING {
    String playerName;
    boolean online;
    short ping;

    public PlayerListItemPacket(String playerName, boolean online, short ping) {
        this.playerName = playerName;
        this.online = online;
        this.ping = ping;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, playerName);
        os.writeBoolean(online);
        os.writeShort(ping);
    }

    @Override
    public int getId() {
        return 0x38;
    }

}
