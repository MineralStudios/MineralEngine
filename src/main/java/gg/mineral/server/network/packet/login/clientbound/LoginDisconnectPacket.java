package gg.mineral.server.network.packet.login.clientbound;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.chat.component.ChatComponentUtil;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginDisconnectPacket implements Packet.OUTGOING {
    private BaseChatComponent baseChatComponent;

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, ChatComponentUtil.toJson(baseChatComponent));
    }

    @Override
    public byte getId() {
        return 0x00;
    }

}
