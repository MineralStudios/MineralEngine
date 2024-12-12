package gg.mineral.server.network.packet.handler

import com.velocitypowered.natives.util.Natives
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageDecoder
import javax.crypto.SecretKey

class PacketDecrypter(sharedSecret: SecretKey) : MessageToMessageDecoder<ByteBuf>() {
    private val cipher by lazy { Natives.cipher.get().forDecryption(sharedSecret) }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        cipher.process(msg)
        out.add(msg.retain())
    }
}