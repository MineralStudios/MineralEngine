package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public record CollectItemPacket(int collectorEntityId, int collectedEntityId) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(collectedEntityId);
        os.writeInt(collectorEntityId);
    }

    @Override
    public byte getId() {
        return 0x0D;
    }
}
