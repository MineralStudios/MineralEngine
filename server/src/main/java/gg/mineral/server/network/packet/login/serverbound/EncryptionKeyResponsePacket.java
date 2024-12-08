package gg.mineral.server.network.packet.login.serverbound;

import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;
import gg.mineral.server.network.connection.ConnectionImpl;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(fluent = true)
public final class EncryptionKeyResponsePacket implements Packet.INCOMING {
    private byte[] sharedSecretBytes, verifyToken;

    @Override
    public void received(Connection connection) {
        if (connection instanceof ConnectionImpl impl) {
            boolean success = impl.authenticate(sharedSecretBytes, verifyToken);
            val config = impl.getServer().getConfig();

            if (success)
                try {
                    impl.loggedIn();
                } catch (Exception e) {
                    e.printStackTrace();
                    connection.disconnect(config.getDisconnectCanNotAuthenticate());
                }
            else
                connection.disconnect(config.getDisconnectCanNotAuthenticate());
        }
    }

    @Override
    public void deserialize(ByteBuf is) {
        short lengthOfSharedSecret = is.readShort();
        sharedSecretBytes = new byte[lengthOfSharedSecret];
        is.readBytes(sharedSecretBytes);
        short lengthOfVerifyToken = is.readShort();
        verifyToken = new byte[lengthOfVerifyToken];
        is.readBytes(verifyToken);
    }
}
