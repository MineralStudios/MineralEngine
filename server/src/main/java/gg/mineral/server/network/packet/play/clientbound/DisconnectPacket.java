package gg.mineral.server.network.packet.play.clientbound;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.chat.component.ChatComponentUtil;
import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record DisconnectPacket(BaseChatComponent baseChatComponent) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeString(os, ChatComponentUtil.toJson(baseChatComponent));
    }

    @Override
    public byte getId() {
        return 0x40;
    }
}
