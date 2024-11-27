package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(fluent = true)
public final class PluginMessagePacket implements Packet.INCOMING, Packet.OUTGOING {
    private String channel;
    private byte[] data;

    @Override
    public void serialize(ByteBuf os) {
        writeString(os, channel);
        os.writeShort(data.length);
        os.writeBytes(data);
    }

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        channel = readString(is);
        short length = is.readShort();
        data = new byte[length];
        is.readBytes(data);
    }

    @Override
    public byte getId() {
        return 0x3F;
    }

}
