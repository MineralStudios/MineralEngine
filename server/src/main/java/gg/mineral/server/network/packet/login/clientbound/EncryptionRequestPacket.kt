package gg.mineral.server.network.packet.login.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf
import java.security.PublicKey

@JvmRecord
data class EncryptionRequestPacket(val serverId: String, val publicKey: PublicKey, val verifyToken: ByteArray) :
    Packet.OUTGOING {
    override fun serialize(os: ByteBuf) {
        writeString(os, serverId)
        val publicKeyBytes = publicKey.encoded
        os.writeShort(publicKeyBytes.size)
        os.writeBytes(publicKeyBytes)
        os.writeShort(verifyToken.size)
        os.writeBytes(verifyToken)
    }

    override fun getId(): Byte {
        return 0x01
    }
}
