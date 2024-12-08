package gg.mineral.server.network.packet.handler;

import java.security.GeneralSecurityException;
import java.util.List;

import javax.crypto.SecretKey;

import com.velocitypowered.natives.encryption.VelocityCipher;
import com.velocitypowered.natives.util.MoreByteBufUtils;
import com.velocitypowered.natives.util.Natives;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.val;

/**
 * Optimized pipeline component using Velocity Natives.
 */
public final class EncryptionHandler extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private final VelocityCipher encryptCipher, decryptCipher;

    /**
     * Creates an instance that applies symmetrical AES encryption using Velocity
     * Natives.
     *
     * @param sharedSecret an AES key
     */
    public EncryptionHandler(SecretKey sharedSecret) {
        try {
            this.encryptCipher = Natives.cipher.get().forEncryption(sharedSecret);
            this.decryptCipher = Natives.cipher.get().forDecryption(sharedSecret);
        } catch (GeneralSecurityException e) {
            throw new AssertionError("Failed to initialize encrypted channel", e);
        }
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        crypt(encryptCipher, msg, out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        crypt(decryptCipher, msg, out);
    }

    private void crypt(VelocityCipher cipher, ByteBuf msg, List<Object> out) {
        val compatibleInput = MoreByteBufUtils.ensureCompatible(msg.alloc(),
                cipher, msg);
        try {
            cipher.process(compatibleInput);
            out.add(compatibleInput.retain());
        } finally {
            compatibleInput.release();
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        encryptCipher.close();
        decryptCipher.close();
    }
}
