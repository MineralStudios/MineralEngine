package gg.mineral.server.network.packet.login.clientbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf
import java.security.PublicKey

@JvmRecord
data class EncryptionRequestPacket(val serverId: String, val publicKey: PublicKey, val verifyToken: ByteArray) :
    Packet.OUTGOING {
    override val id: Byte
        get() = 0x01

    override fun serialize(os: ByteBuf) {
        os.writeString(serverId)
        val publicKeyBytes = publicKey.encoded
        os.writeShort(publicKeyBytes.size)
        os.writeBytes(publicKeyBytes)
        os.writeShort(verifyToken.size)
        os.writeBytes(verifyToken)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptionRequestPacket

        if (serverId != other.serverId) return false
        if (publicKey != other.publicKey) return false
        if (!verifyToken.contentEquals(other.verifyToken)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serverId.hashCode()
        result = 31 * result + publicKey.hashCode()
        result = 31 * result + verifyToken.contentHashCode()
        return result
    }
}
