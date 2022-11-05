package gg.mineral.server.network.packet;

import java.util.List;

import gg.mineral.server.network.protocol.ProtocolState;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

public class PacketDecoder extends ByteToMessageDecoder {

    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf buf, List<Object> packets)
            throws Exception {
        if (!channelHandlerContext.channel().isActive() || !buf.isReadable()) {
            buf.release();
            return;
        }

        IncomingPacketRegistry INCOMING_PACKET_REGISTRY = channelHandlerContext.channel()
                .attr(ProtocolState.ATTRIBUTE_KEY)
                .get();

        packets.addAll(PacketSerializer.deserialize(buf, INCOMING_PACKET_REGISTRY));
    }
}
