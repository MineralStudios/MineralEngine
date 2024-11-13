package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class BlockBreakAnimationPacket implements Packet.OUTGOING {
    int entityId, x, y, z;
    byte destroyStage;

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, entityId);
        os.writeInt(x);
        os.writeInt(y);
        os.writeInt(z);
        os.writeByte(destroyStage);
    }

    @Override
    public byte getId() {
        return 0x25;
    }

}
