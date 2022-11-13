package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import gg.mineral.server.world.property.Difficulty;
import io.netty.buffer.ByteBuf;

public class ClientSettingsPacket implements Packet.INCOMING {
    String locale;
    byte viewDistance, chatFlags;
    Difficulty difficulty;
    boolean chatColors, showCape;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        locale = ByteBufUtil.readString(is);
        viewDistance = is.readByte();
        chatFlags = is.readByte();
        chatColors = is.readBoolean();
        difficulty = Difficulty.fromId(is.readByte());
        showCape = is.readBoolean();
    }
}
