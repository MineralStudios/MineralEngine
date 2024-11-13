package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.val;

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
        if (animationId == 1) {
            val player = EntityManager.get(connection);

            if (player == null)
                return;

            player.swingArm();
        }

    }

    @Override
    public void deserialize(ByteBuf is) {
        entityId = is.readInt();
        animationId = is.readUnsignedByte();
    }

    @Override
    public byte getId() {
        return 0x0B;
    }

}
