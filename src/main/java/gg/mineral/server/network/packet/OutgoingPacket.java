package gg.mineral.server.network.packet;

import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public abstract class OutgoingPacket extends Packet {
    public abstract void serialize(ByteBuf os);

    public ByteBuf write() {
        ByteBuf data = Unpooled.buffer();
        ByteBufUtil.writeVarInt(data, getId());
        serialize(data);
        int length = data.writerIndex();
        int lengthSize = ByteBufUtil.getVarIntSize(length);
        ByteBuf os = Unpooled.buffer(lengthSize + length);
        ByteBufUtil.writeVarInt(os, length);
        os.writeBytes(data);
        return os;
    }
}
