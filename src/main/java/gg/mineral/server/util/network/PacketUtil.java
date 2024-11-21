package gg.mineral.server.util.network;

import java.nio.ByteBuffer;
import java.util.List;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.network.packet.Packet.INCOMING;
import gg.mineral.server.network.packet.registry.PacketRegistry;
import gg.mineral.server.util.collection.GlueList;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue.Consumer;
import lombok.val;

public class PacketUtil {

    public static List<Packet.INCOMING> deserialize(ByteBuf data, PacketRegistry<INCOMING> packetRegistry) {
        val packets = new GlueList<INCOMING>();

        processPackets(data, packetBuf -> {
            try {
                byte id = packetBuf.readByte();
                val packet = packetRegistry.create(id);
                packet.deserialize(packetBuf);
                packets.add(packet);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (packetBuf.refCnt() > 0)
                    packetBuf.release();
            }
        });

        data.discardReadBytes();
        return packets;
    }

    public static ByteBuf serialize(Packet.OUTGOING packet) {
        val data = Unpooled.buffer();
        ByteBufUtil.writeVarInt(data, packet.getId());
        packet.serialize(data);
        int length = data.writerIndex();
        int lengthSize = ByteBufUtil.getVarIntSize(length);
        val os = Unpooled.buffer(lengthSize + length);
        ByteBufUtil.writeVarInt(os, length);
        os.writeBytes(data);
        return os;
    }

    public static void processPackets(ByteBuf buf, Consumer<ByteBuf> consumer) {
        buf.markReaderIndex();
        val lengthBytes = new byte[3];

        for (int position = 0; position < lengthBytes.length; ++position) {
            if (!buf.isReadable()) {
                buf.resetReaderIndex();
                return;
            }

            lengthBytes[position] = buf.readByte();

            if (lengthBytes[position] < 0)
                continue;

            val byteBuf = ByteBuffer.wrap(lengthBytes);

            try {
                int length = ByteBufUtil.readVarInt(byteBuf);
                if (buf.readableBytes() < length) {
                    buf.resetReaderIndex();
                    return;
                }
                val splitBuf = buf.readBytes(length);
                consumer.accept(splitBuf);
            } finally {
                byteBuf.clear();
            }
            return;
        }
    }
}
