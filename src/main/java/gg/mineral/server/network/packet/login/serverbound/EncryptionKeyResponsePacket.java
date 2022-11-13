package gg.mineral.server.network.packet.login.serverbound;

import gg.mineral.server.entity.living.human.Player;
import gg.mineral.server.entity.living.human.manager.PlayerManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.network.packet.login.clientbound.LoginSuccessPacket;
import gg.mineral.server.network.protocol.ProtocolState;
import gg.mineral.server.util.messages.Messages;
import io.netty.buffer.ByteBuf;

public class EncryptionKeyResponsePacket implements Packet.INCOMING {
    byte[] sharedSecretBytes, verifyToken;

    @Override
    public void received(Connection connection) {
        Player player = PlayerManager.get(p -> p.getConnection().equals(connection));
        player.authenticate(sharedSecretBytes, verifyToken).whenComplete((success, ex) -> {
            if (success) {
                connection.setProtocolState(ProtocolState.PLAY);
                connection.sendPacket(new LoginSuccessPacket(player.getUUID(), player.getName()));
                return;
            }

            player.disconnect(Messages.DISCONNECT_CAN_NOT_AUTHENTICATE);
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
}
