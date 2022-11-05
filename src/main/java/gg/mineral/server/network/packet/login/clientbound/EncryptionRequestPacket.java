package gg.mineral.server.network.packet.login.clientbound;

import java.security.PublicKey;

import gg.mineral.server.network.packet.OutgoingPacket;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class EncryptionRequestPacket extends OutgoingPacket {
    String serverId;
    short lengthOfPublicKey, lengthOfVerifyToken;
    byte[] publicKeyBytes, verifyToken;

    public EncryptionRequestPacket(String serverId,
            PublicKey publicKey, byte[] verifyToken) {
        this.serverId = serverId;
        this.publicKeyBytes = publicKey.getEncoded();
        this.verifyToken = verifyToken;
        this.lengthOfPublicKey = (short) publicKeyBytes.length;
        this.lengthOfVerifyToken = (short) verifyToken.length;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, serverId);
        os.writeShort(lengthOfPublicKey);
        os.writeBytes(publicKeyBytes);
        os.writeShort(lengthOfVerifyToken);
        os.writeBytes(verifyToken);
    }

    @Override
    public int getId() {
        return 0x01;
    }

}
