package gg.mineral.server.network.packet.login.clientbound;

import java.security.PublicKey;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class EncryptionRequestPacket implements Packet.OUTGOING {
    String serverId;
    byte[] publicKeyBytes, verifyToken;

    public EncryptionRequestPacket(String serverId,
            PublicKey publicKey, byte[] verifyToken) {
        this.serverId = serverId;
        this.publicKeyBytes = publicKey.getEncoded();
        this.verifyToken = verifyToken;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, serverId);
        os.writeShort(publicKeyBytes.length);
        os.writeBytes(publicKeyBytes);
        os.writeShort(verifyToken.length);
        os.writeBytes(verifyToken);
    }

    @Override
    public byte getId() {
        return 0x01;
    }

}
