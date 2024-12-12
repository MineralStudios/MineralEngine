package gg.mineral.server.network.packet.login.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.network.connection.ConnectionImpl
import gg.mineral.server.network.protocol.ProtocolState
import io.netty.buffer.ByteBuf
import java.util.concurrent.CompletableFuture

class EncryptionKeyResponsePacket(
    var sharedSecretBytes: ByteArray? = null,
    var verifyToken: ByteArray? = null
) : Packet.INCOMING {
    override fun received(connection: Connection) {
        if (connection is ConnectionImpl) {
            CompletableFuture.supplyAsync(
                { connection.authenticate(sharedSecretBytes!!, verifyToken!!) },
                connection.server.asyncExecutor
            )
                .thenAccept { secretKey ->
                    val config = connection.server.config
                    if (secretKey != null) try {
                        connection.enableEncryption(secretKey)
                        connection.loginSuccess()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        connection.disconnect(config.disconnectCanNotAuthenticate)
                    } else connection.disconnect(config.disconnectCanNotAuthenticate)
                }.thenAcceptAsync({
                    if (connection.protocolState == ProtocolState.PLAY) connection.spawnPlayer()
                }, connection.server.tickExecutor)
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
