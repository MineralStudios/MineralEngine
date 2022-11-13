package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class PluginMessagePacket implements Packet.INCOMING {
    String channel;
    byte[] data;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        channel = ByteBufUtil.readString(is);
        short length = is.readShort();
        data = new byte[length];
        is.readBytes(data);
    }

    @Override
    public int getId() {
        return 0x17;
    }

}
