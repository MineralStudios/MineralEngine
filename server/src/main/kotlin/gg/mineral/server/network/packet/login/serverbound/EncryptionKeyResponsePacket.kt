package gg.mineral.server.network.packet.login.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.network.connection.ConnectionImpl
import gg.mineral.server.network.protocol.ProtocolState
import io.netty.buffer.ByteBuf
import kotlinx.coroutines.withContext

class EncryptionKeyResponsePacket(
    var sharedSecretBytes: ByteArray? = null,
    var verifyToken: ByteArray? = null
) : Packet.Incoming, Packet.AsyncHandler {
    override suspend fun receivedAsync(connection: Connection) {
        if (connection !is ConnectionImpl) return
        val secretKey = connection.authenticate(sharedSecretBytes!!, verifyToken!!)

        val config = connection.server.config
        if (secretKey != null) {
            try {
                connection.enableEncryption(secretKey)
                connection.loginSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                connection.disconnect(config.disconnectCanNotAuthenticate)
            }
        } else connection.disconnect(config.disconnectCanNotAuthenticate)

        withContext(connection.server.syncDispatcher) {
            if (connection.protocolState == ProtocolState.PLAY) connection.spawnPlayer()
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
