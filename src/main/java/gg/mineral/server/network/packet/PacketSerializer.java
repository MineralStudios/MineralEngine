package gg.mineral.server.network.packet;

import java.util.List;

import gg.mineral.server.util.collection.GlueList;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class PacketSerializer {

    public static List<IncomingPacket> deserialize(ByteBuf data, IncomingPacketRegistry incomingPacketRegistry)
            throws InstantiationException, IllegalAccessException {
        List<IncomingPacket> packets = new GlueList<>();

        for (ByteBuf packetBuf : splitPackets(data)) {
            int id = ByteBufUtil.readVarInt(packetBuf);

            Class<? extends IncomingPacket> packetClass = incomingPacketRegistry.get(id);
            IncomingPacket packet = packetClass.newInstance();
            packet.deserialize(packetBuf);
            packets.add(packet);
        }

        data.discardReadBytes();
        return packets;
    }

    public static List<ByteBuf> splitPackets(ByteBuf buf) {
        List<ByteBuf> bufList = new GlueList<>();
        buf.markReaderIndex();
        byte[] lengthBytes = new byte[3];

        for (int position = 0; position < lengthBytes.length; ++position) {
            if (!buf.isReadable()) {
                buf.resetReaderIndex();
                return bufList;
            }

            lengthBytes[position] = buf.readByte();

            if (lengthBytes[position] < 0) {
                continue;
            }

            ByteBuf byteBuf = Unpooled.wrappedBuffer(lengthBytes);

            try {
                int length = ByteBufUtil.readVarInt(byteBuf);
                if (buf.readableBytes() < length) {
                    buf.resetReaderIndex();
                    return bufList;
                }
                bufList.add(buf.readBytes(length));
            } finally {
                byteBuf.release();
            }
            return bufList;
        }
        return bufList;
    }
}
