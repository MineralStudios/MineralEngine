package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class CollectItemPacket implements Packet.OUTGOING {

    int collectorEntityId, collectedEntityId;

    public CollectItemPacket(int collectorEntityId, int collectedEntityId) {
        this.collectorEntityId = collectorEntityId;
        this.collectedEntityId = collectedEntityId;
    }

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
