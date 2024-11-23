package gg.mineral.server.network.packet.login.clientbound;

import java.util.UUID;

import com.eatthepath.uuid.FastUUID;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public record LoginSuccessPacket(UUID uuid, String username) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, FastUUID.toString(uuid));
        ByteBufUtil.writeString(os, username);
    }

    @Override
    public byte getId() {
        return 0x02;
    }
}
