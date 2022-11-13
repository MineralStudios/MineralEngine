package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class AnimationPacket implements Packet.INCOMING, Packet.OUTGOING {

    int entityId;
    short animationId;

    public AnimationPacket(int entityId, short animationId) {
        this.entityId = entityId;
        this.animationId = animationId;
    }

    public AnimationPacket() {
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeVarInt(os, entityId);
        os.writeByte(animationId);
    }

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        entityId = is.readInt();
        animationId = is.readByte();
    }

    @Override
    public int getId() {
        return 0x0B;
    }

}
