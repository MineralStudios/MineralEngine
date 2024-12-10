package gg.mineral.server.network.packet.login.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.network.connection.ConnectionImpl
import io.netty.buffer.ByteBuf
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import lombok.experimental.Accessors

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
class EncryptionKeyResponsePacket : Packet.INCOMING {
    private var sharedSecretBytes: ByteArray
    private var verifyToken: ByteArray

    override fun received(connection: Connection) {
        if (connection is ConnectionImpl) {
            val success = connection.authenticate(sharedSecretBytes, verifyToken)
            val config = connection.server.config

            if (success) try {
                connection.loggedIn()
            } catch (e: Exception) {
                e.printStackTrace()
                connection.disconnect(config.disconnectCanNotAuthenticate)
            }
            else connection.disconnect(config.disconnectCanNotAuthenticate)
        }
    }

    override fun deserialize(`is`: ByteBuf) {
        val lengthOfSharedSecret = `is`.readShort()
        sharedSecretBytes = ByteArray(lengthOfSharedSecret.toInt())
        `is`.readBytes(sharedSecretBytes)
        val lengthOfVerifyToken = `is`.readShort()
        verifyToken = ByteArray(lengthOfVerifyToken.toInt())
        `is`.readBytes(verifyToken)
    }
}
