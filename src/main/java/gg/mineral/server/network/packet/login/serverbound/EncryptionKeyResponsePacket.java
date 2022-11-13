package gg.mineral.server.network.packet.login.serverbound;

import gg.mineral.server.entity.living.human.Player;
import gg.mineral.server.entity.living.human.manager.PlayerManager;
import gg.mineral.server.entity.living.human.property.Gamemode;
import gg.mineral.server.entity.living.human.property.PlayerAbilities;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.network.packet.login.clientbound.LoginSuccessPacket;
import gg.mineral.server.network.packet.play.bidirectional.HeldItemChangePacket;
import gg.mineral.server.network.packet.play.bidirectional.PlayerAbilitiesPacket;
import gg.mineral.server.network.packet.play.clientbound.JoinGamePacket;
import gg.mineral.server.network.packet.play.clientbound.SpawnPositionPacket;
import gg.mineral.server.network.protocol.ProtocolState;
import gg.mineral.server.util.messages.Messages;
import gg.mineral.server.world.property.Difficulty;
import gg.mineral.server.world.property.Dimension;
import gg.mineral.server.world.property.LevelType;
import io.netty.buffer.ByteBuf;

public class EncryptionKeyResponsePacket implements Packet.INCOMING {
    byte[] sharedSecretBytes, verifyToken;

    @Override
    public void received(Connection connection) {
        Player player = PlayerManager.get(p -> p.getConnection().equals(connection));
        player.authenticate(sharedSecretBytes, verifyToken).whenComplete((success, ex) -> {
            if (success) {
                connection.setProtocolState(ProtocolState.PLAY);
                connection.sendPacket(new LoginSuccessPacket(player.getUUID(), player.getName()),
                        new JoinGamePacket(0, Gamemode.CREATIVE, Dimension.OVERWORLD, Difficulty.PEACEFUL,
                                LevelType.FLAT, (short) 1000),
                        new SpawnPositionPacket(0, 0, 0),
                        new PlayerAbilitiesPacket(new PlayerAbilities(false, false, false, true, true, 0.05f, 0.1f)),
                        new HeldItemChangePacket((short) 0));
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
