package gg.mineral.server.network.packet.login.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.messages.Messages;
import io.netty.buffer.ByteBuf;

public class EncryptionKeyResponsePacket implements Packet.INCOMING {
    byte[] sharedSecretBytes, verifyToken;

    @Override
    public void received(Connection connection) {

        boolean success = connection.authenticate(sharedSecretBytes, verifyToken);

        if (success) {
            connection.loggedIn();
            return;
        }

        connection.disconnect(Messages.DISCONNECT_CAN_NOT_AUTHENTICATE);
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
