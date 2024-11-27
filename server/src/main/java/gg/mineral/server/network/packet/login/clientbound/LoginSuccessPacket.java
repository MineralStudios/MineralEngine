package gg.mineral.server.network.packet.login.clientbound;

import java.util.UUID;

import com.eatthepath.uuid.FastUUID;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record LoginSuccessPacket(UUID uuid, String username) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeString(os, FastUUID.toString(uuid), username);
    }

    @Override
    public byte getId() {
        return 0x02;
    }
}
