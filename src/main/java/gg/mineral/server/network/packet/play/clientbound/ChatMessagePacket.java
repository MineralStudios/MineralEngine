package gg.mineral.server.network.packet.play.clientbound;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.chat.component.ChatComponentUtil;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class ChatMessagePacket implements Packet.OUTGOING {

    BaseChatComponent baseChatComponent;

    public ChatMessagePacket(BaseChatComponent baseChatComponent) {
        this.baseChatComponent = baseChatComponent;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, ChatComponentUtil.toJson(baseChatComponent));
    }

    @Override
    public byte getId() {
        return 0x02;
    }

}
