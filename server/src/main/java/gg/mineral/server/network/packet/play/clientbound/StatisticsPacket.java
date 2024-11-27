package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.val;

public final record StatisticsPacket(Object2IntMap<String> statistics) implements Packet.OUTGOING {

    @Override
    public void serialize(ByteBuf os) {
        writeVarInt(os, statistics.size());

        for (val entry : statistics.object2IntEntrySet()) {
            writeString(os, entry.getKey());
            writeVarInt(os, entry.getIntValue());
        }
    }

    @Override
    public byte getId() {
        return 0x37;
    }
}
