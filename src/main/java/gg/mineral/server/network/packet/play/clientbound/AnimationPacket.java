package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class AnimationPacket implements Packet.OUTGOING {

    int entityId;
    short animationId;

    public AnimationPacket(int entityId, short animationId) {
        this.entityId = entityId;
        this.animationId = animationId;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, entityId);
        os.writeByte(animationId);
    }

    @Override
    public int getId() {
        return 0x0B;
    }

}
