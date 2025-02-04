package gg.mineral.server.network.packet.handler

import gg.mineral.api.network.packet.Packet
import gg.mineral.api.network.packet.rw.ByteWriter
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder

class PacketEncoder : MessageToByteEncoder<Packet.Outgoing>(), ByteWriter {
    override fun encode(ctx: ChannelHandlerContext, packet: Packet.Outgoing, out: ByteBuf) {
        out.writeVarInt(packet.id.toInt())
        packet.serialize(out)
    }
}
