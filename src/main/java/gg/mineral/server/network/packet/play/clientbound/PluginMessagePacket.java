package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class PluginMessagePacket implements Packet.OUTGOING {
    String channel;
    byte[] data;

    public PluginMessagePacket(String channel, byte[] data) {
        this.channel = channel;
        this.data = data;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, channel);
        os.writeShort(data.length);
        os.writeBytes(data);
    }

    @Override
    public int getId() {
        return 0x3F;
    }

}
