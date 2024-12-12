package gg.mineral.server.network.packet.handler

import gg.mineral.api.network.packet.rw.ByteWriter
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandler
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder


@ChannelHandler.Sharable
class PacketPrepender : MessageToByteEncoder<ByteBuf>(), ByteWriter {
    companion object {
        val INSTANCE = PacketPrepender()
    }

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        val length = msg.readableBytes()
        out.writeVarInt(length)
        out.writeBytes(msg)
    }

    @Throws(Exception::class)
    override fun allocateBuffer(ctx: ChannelHandlerContext, msg: ByteBuf, preferDirect: Boolean): ByteBuf {
        val length = msg.readableBytes()
        return ctx.alloc().directBuffer(getVarIntSize(length) + length)
    }
}
