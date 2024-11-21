package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.val;

public record TabCompletePacket(List<String> completions) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, completions.size());

        for (val completion : completions)
            ByteBufUtil.writeString(os, completion);
    }

    @Override
    public byte getId() {
        return 0x3A;
    }
}
