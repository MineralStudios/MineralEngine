package gg.mineral.server.network.packet.handler

import gg.mineral.api.network.packet.rw.ByteReader
import gg.mineral.server.network.protocol.ProtocolState
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder

class PacketDecoder : ByteToMessageDecoder(), ByteReader {
    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, packets: MutableList<Any>) {
        if (!ctx.channel().isActive || !buf.isReadable) return

        val packetRegistry = ctx.channel().attr(ProtocolState.ATTRIBUTE_KEY).get()

        buf.markReaderIndex()

        val length = buf.readVarInt()
        if (length == -1) {
            buf.resetReaderIndex()
            return
        }

        if (buf.readableBytes() < length) {
            buf.resetReaderIndex()
            return
        }

        val packetData = buf.readRetainedSlice(length)

        try {
            val id = packetData.readByte()
            val packet = packetRegistry.create(id)
            packet.deserialize(packetData)
            packets.add(packet)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Release the packet buffer after processing
            if (packetData.refCnt() > 0) packetData.release()
        }
        // Discard read bytes to keep buffer tidy
        buf.discardReadBytes()
    }
}
