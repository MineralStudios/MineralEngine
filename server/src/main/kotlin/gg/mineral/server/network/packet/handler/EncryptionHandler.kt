package gg.mineral.server.network.packet.handler

import com.velocitypowered.natives.encryption.VelocityCipher
import com.velocitypowered.natives.util.MoreByteBufUtils
import com.velocitypowered.natives.util.Natives
import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageCodec
import java.security.GeneralSecurityException
import javax.crypto.SecretKey

/**
 * Optimized pipeline component using Velocity Natives.
 */
class EncryptionHandler(sharedSecret: SecretKey?) : MessageToMessageCodec<ByteBuf, ByteBuf>() {
    private var encryptCipher: VelocityCipher? = null
    private var decryptCipher: VelocityCipher? = null

    /**
     * Creates an instance that applies symmetrical AES encryption using Velocity
     * Natives.
     *
     * @param sharedSecret an AES key
     */
    init {
        try {
            this.encryptCipher = Natives.cipher.get().forEncryption(sharedSecret)
            this.decryptCipher = Natives.cipher.get().forDecryption(sharedSecret)
        } catch (e: GeneralSecurityException) {
            throw AssertionError("Failed to initialize encrypted channel", e)
        }
    }

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        crypt(encryptCipher!!, msg, out)
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        crypt(decryptCipher!!, msg, out)
    }

    private fun crypt(cipher: VelocityCipher, msg: ByteBuf, out: MutableList<Any>) {
        val compatibleInput = MoreByteBufUtils.ensureCompatible(
            msg.alloc(),
            cipher, msg
        )
        try {
            cipher.process(compatibleInput)
            out.add(compatibleInput.retain())
        } finally {
            compatibleInput.release()
        }
    }

    @Throws(Exception::class)
    override fun handlerRemoved(ctx: ChannelHandlerContext) {
        encryptCipher!!.close()
        decryptCipher!!.close()
    }
}
