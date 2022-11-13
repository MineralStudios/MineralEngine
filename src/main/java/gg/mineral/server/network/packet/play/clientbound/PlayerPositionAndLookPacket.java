package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class PlayerPositionAndLookPacket implements Packet.OUTGOING {
    double x, y, z;
    float yaw, pitch;
    boolean onGround;

    public PlayerPositionAndLookPacket(double x, double y, double z, float yaw, float pitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeDouble(x);
        os.writeDouble(y);
        os.writeDouble(z);
        os.writeFloat(yaw);
        os.writeFloat(pitch);
        os.writeBoolean(onGround);
    }

    @Override
    public int getId() {
        return 0x08;
    }

}
