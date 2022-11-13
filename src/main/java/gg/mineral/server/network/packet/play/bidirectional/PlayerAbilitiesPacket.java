package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class PlayerAbilitiesPacket implements Packet.INCOMING, Packet.OUTGOING {
    byte flags;
    float flyingSpeed, walkingSpeed;

    public PlayerAbilitiesPacket(byte flags, float flyingSpeed, float walkingSpeed) {
        this.flags = flags;
        this.flyingSpeed = flyingSpeed;
        this.walkingSpeed = walkingSpeed;
    }

    public PlayerAbilitiesPacket() {
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(flags);
        os.writeFloat(flyingSpeed);
        os.writeFloat(walkingSpeed);
    }

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        flags = is.readByte();
        flyingSpeed = is.readFloat();
        walkingSpeed = is.readFloat();
    }

    @Override
    public int getId() {
        return 0x39;
    }

}
