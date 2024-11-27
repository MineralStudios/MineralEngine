package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record PlayerPositionAndLookPacket(double x, double headY, double z, float yaw, float pitch,
        boolean onGround)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeDouble(x);
        os.writeDouble(headY);
        os.writeDouble(z);
        os.writeFloat(yaw);
        os.writeFloat(pitch);
        os.writeBoolean(onGround);
    }

    @Override
    public byte getId() {
        return 0x08;
    }
}
