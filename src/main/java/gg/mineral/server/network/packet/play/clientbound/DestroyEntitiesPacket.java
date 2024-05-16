package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DestroyEntitiesPacket implements Packet.OUTGOING {
    final int[] entityIds;

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(entityIds.length);
        ByteBufUtil.writeIntArray(os, entityIds);
    }

    @Override
    public byte getId() {
        return 0x13;
    }

}
