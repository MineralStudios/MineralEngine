package gg.mineral.server.network.packet.handler

import gg.mineral.api.network.packet.rw.ByteReader
import gg.mineral.server.network.protocol.ProtocolState
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.util.function.Consumer

class PacketDecoder : ByteToMessageDecoder(), ByteReader {
    @Throws(Exception::class)
    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, packets: MutableList<Any>) {
        if (!ctx.channel().isActive || !buf.isReadable) return

        val packetRegistry = ctx.channel().attr(ProtocolState.ATTRIBUTE_KEY).get()

        processPacket(buf) { packetBuf: ByteBuf ->
            try {
                val id = packetBuf.readByte()
                val packet = packetRegistry.create(id)
                packet.deserialize(packetBuf)
                packets.add(packet)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Release the packet buffer after processing
                if (packetBuf.refCnt() > 0) packetBuf.release()
            }
        }

        // Discard read bytes to keep buffer tidy
        buf.discardReadBytes()
    }

    /**
     * Attempts to read a single packet from the buffer and pass it to the consumer.
     * If not enough data is present to form a complete packet, the buffer's reader
     * index is reset and this method returns without consuming any data.
     */
    private fun processPacket(buf: ByteBuf, consumer: Consumer<ByteBuf>) {
        buf.markReaderIndex()

        val length = readVarInt(buf)
        if (length == -1) {
            buf.resetReaderIndex()
            return
        }

        if (buf.readableBytes() < length) {
            buf.resetReaderIndex()
            return
        }

        val packetData = buf.readRetainedSlice(length)
        consumer.accept(packetData)
    }
}
