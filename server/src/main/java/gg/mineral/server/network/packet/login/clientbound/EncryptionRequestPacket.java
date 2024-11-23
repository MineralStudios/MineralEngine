package gg.mineral.server.network.packet.login.clientbound;

import java.security.PublicKey;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.val;

public record EncryptionRequestPacket(String serverId, PublicKey publicKey, byte[] verifyToken)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, serverId);
        val publicKeyBytes = publicKey.getEncoded();
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
