package gg.mineral.server.network.packet.handler;

import java.util.List;

import gg.mineral.api.network.packet.rw.ByteReader;
import gg.mineral.server.network.protocol.ProtocolState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.val;

public class PacketDecoder extends ByteToMessageDecoder implements ByteReader {

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> packets)
            throws Exception {
        if (!channelHandlerContext.channel().isActive() || !buf.isReadable()) {
            if (buf.refCnt() > 0)
                buf.release();
            return;
        }

        val incomingPacketRegistry = channelHandlerContext.channel()
                .attr(ProtocolState.ATTRIBUTE_KEY)
                .get();

        packets.addAll(deserialize(buf, incomingPacketRegistry));
    }
}
