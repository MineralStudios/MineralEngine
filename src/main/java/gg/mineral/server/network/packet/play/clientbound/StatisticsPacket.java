package gg.mineral.server.network.packet.play.clientbound;

import java.util.Map;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class StatisticsPacket implements Packet.OUTGOING {
    Map<String, Integer> statistics;

    public StatisticsPacket(Map<String, Integer> statistics) {
        this.statistics = statistics;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, statistics.size());

        for (Map.Entry<String, Integer> entry : statistics.entrySet()) {
            ByteBufUtil.writeString(os, entry.getKey());
            ByteBufUtil.writeVarInt(os, entry.getValue());
        }
    }

    @Override
    public int getId() {
        return 0x37;
    }

}
