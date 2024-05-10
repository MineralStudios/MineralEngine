package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class AnimationPacket implements Packet.INCOMING, Packet.OUTGOING {

    int entityId;
    short animationId;

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
    public byte getId() {
        return 0x0B;
    }

}
