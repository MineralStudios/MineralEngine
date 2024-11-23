package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.val;

public record StatisticsPacket(Object2IntMap<String> statistics) implements Packet.OUTGOING {

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, statistics.size());

        for (val entry : statistics.object2IntEntrySet()) {
            ByteBufUtil.writeString(os, entry.getKey());
            ByteBufUtil.writeVarInt(os, entry.getIntValue());
        }
    }

    @Override
    public byte getId() {
        return 0x37;
    }
}
