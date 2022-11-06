package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class CollectItemPacket implements Packet.OUTGOING {

    int collectorEntityId, collectedEntityId;

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(collectedEntityId);
        os.writeInt(collectorEntityId);
    }

    @Override
    public int getId() {
        return 0x0D;
    }

}
