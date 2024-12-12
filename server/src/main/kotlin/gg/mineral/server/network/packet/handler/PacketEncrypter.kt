package gg.mineral.server.network.packet.handler

import com.velocitypowered.natives.util.Natives
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToByteEncoder
import javax.crypto.SecretKey

class PacketEncrypter(sharedSecret: SecretKey) : MessageToByteEncoder<ByteBuf>() {
    private val cipher by lazy { Natives.cipher.get().forEncryption(sharedSecret) }

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: ByteBuf) {
        cipher.process(msg)
        out.writeBytes(msg)
    }
}