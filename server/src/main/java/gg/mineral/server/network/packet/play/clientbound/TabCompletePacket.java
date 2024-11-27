package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;
import lombok.val;

public final record TabCompletePacket(List<String> completions) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeVarInt(os, completions.size());

        for (val completion : completions)
            writeString(os, completion);
    }

    @Override
    public byte getId() {
        return 0x3A;
    }
}
