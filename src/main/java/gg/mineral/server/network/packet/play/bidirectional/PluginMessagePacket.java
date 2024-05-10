package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PluginMessagePacket implements Packet.INCOMING, Packet.OUTGOING {
    String channel;
    byte[] data;

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, channel);
        os.writeShort(data.length);
        os.writeBytes(data);
    }

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
    public byte getId() {
        return 0x3F;
    }

}
