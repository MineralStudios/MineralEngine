package gg.mineral.server.network.packet.login.serverbound;

import gg.mineral.server.entity.Player;
import gg.mineral.server.entity.PlayerManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.IncomingPacket;
import gg.mineral.server.network.packet.login.clientbound.LoginSuccessPacket;
import gg.mineral.server.util.messages.Messages;
import io.netty.buffer.ByteBuf;

public class EncryptionKeyResponsePacket extends IncomingPacket {
    byte[] sharedSecretBytes, verifyToken;

    @Override
    public void received(Connection connection) {
        Player player = PlayerManager.get(p -> p.getConnection().equals(connection));
        player.authenticate(sharedSecretBytes, verifyToken).whenComplete((success, ex) -> {
            if (success) {
                connection.sendPacket(new LoginSuccessPacket(player.getUUID(), player.getName()));
                return;
            }

            PlayerManager.disconnect(player, Messages.DISCONNECT_CAN_NOT_AUTHENTICATE);
        });
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

    @Override
    public int getId() {
        return 0x01;
    }

}
