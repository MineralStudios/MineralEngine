package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.math.MathUtil;
import io.netty.buffer.ByteBuf;

public class EntityRelativeMovePacket implements Packet.OUTGOING {

    int entityId;
    byte deltaX, deltaY, deltaZ;

    public EntityRelativeMovePacket(int entityId, double deltaX, double deltaY, double deltaZ) {
        this.entityId = entityId;
        this.deltaX = MathUtil.toFixedPointByte(deltaX);
        this.deltaY = MathUtil.toFixedPointByte(deltaY);
        this.deltaZ = MathUtil.toFixedPointByte(deltaZ);
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeByte(deltaX);
        os.writeByte(deltaY);
        os.writeByte(deltaZ);
    }

    @Override
    public int getId() {
        return 0x15;
    }

}
