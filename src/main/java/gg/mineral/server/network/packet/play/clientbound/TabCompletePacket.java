package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class TabCompletePacket implements Packet.OUTGOING {
    List<String> completions;

    public TabCompletePacket(List<String> completions) {
        this.completions = completions;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, completions.size());

        for (String completion : completions) {
            ByteBufUtil.writeString(os, completion);
        }
    }

    @Override
    public int getId() {
        return 0x3A;
    }

}
