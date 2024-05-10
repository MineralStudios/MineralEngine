package gg.mineral.server.util.network;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.Callable;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.network.packet.Packet.INCOMING;
import gg.mineral.server.network.packet.registry.IncomingPacketRegistry;
import gg.mineral.server.util.collection.GlueList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;

public class PacketUtil {

    public static List<Packet.INCOMING> deserialize(ByteBuf data, IncomingPacketRegistry incomingPacketRegistry) {
        List<Packet.INCOMING> packets = new GlueList<>();

        processPackets(data, packetBuf -> {
            try {
                byte id = packetBuf.readByte();

                Callable<INCOMING> packetBuilder = incomingPacketRegistry.get(id);
                Packet.INCOMING packet = packetBuilder.call();
                packet.deserialize(packetBuf);
                packets.add(packet);
                packetBuf.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        data.discardReadBytes();
        return packets;
    }

    public static ByteBuf serialize(Packet.OUTGOING packet) {
        ByteBuf data = Unpooled.buffer();
        ByteBufUtil.writeVarInt(data, packet.getId());
        packet.serialize(data);
        int length = data.writerIndex();
        int lengthSize = ByteBufUtil.getVarIntSize(length);
        ByteBuf os = Unpooled.buffer(lengthSize + length);
        ByteBufUtil.writeVarInt(os, length);
        os.writeBytes(data);
        return os;
    }

    public static void processPackets(ByteBuf buf, Consumer<ByteBuf> consumer) {
        buf.markReaderIndex();
        byte[] lengthBytes = new byte[3];

        for (int position = 0; position < lengthBytes.length; ++position) {
            if (!buf.isReadable()) {
                buf.resetReaderIndex();
                return;
            }

            lengthBytes[position] = buf.readByte();

            if (lengthBytes[position] < 0)
                continue;

            ByteBuffer byteBuf = ByteBuffer.wrap(lengthBytes);

            try {
                int length = ByteBufUtil.readVarInt(byteBuf);
                if (buf.readableBytes() < length) {
                    buf.resetReaderIndex();
                    return;
                }
                ByteBuf splitBuf = buf.readBytes(length);
                consumer.accept(splitBuf);
            } finally {
                byteBuf.clear();
            }
            return;
        }
        return;
    }
}
