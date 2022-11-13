package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class DestroyEntitiesPacket implements Packet.OUTGOING {
    byte count;
    int[] entityIds;

    public DestroyEntitiesPacket(byte count, int... entityIds) {
        this.count = count;
        this.entityIds = entityIds;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(count);
        ByteBufUtil.writeIntArray(os, entityIds);
    }

    @Override
    public int getId() {
        return 0x13;
    }

}
