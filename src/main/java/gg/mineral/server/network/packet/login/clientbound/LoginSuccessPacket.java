package gg.mineral.server.network.packet.login.clientbound;

import java.util.UUID;

import com.eatthepath.uuid.FastUUID;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginSuccessPacket implements Packet.OUTGOING {
    private UUID uuid;
    private String username;

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
