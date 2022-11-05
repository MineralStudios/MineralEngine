package gg.mineral.server.network.packet.login.clientbound;

import java.util.UUID;

import gg.mineral.server.network.packet.OutgoingPacket;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class LoginSuccessPacket extends OutgoingPacket {
    UUID uuid;
    String username;

    public LoginSuccessPacket(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeUuid(os, uuid);
        ByteBufUtil.writeString(os, username);
    }

    @Override
    public int getId() {
        return 0x02;
    }

}
