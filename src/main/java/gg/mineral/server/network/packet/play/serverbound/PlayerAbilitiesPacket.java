package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class PlayerAbilitiesPacket implements Packet.INCOMING {
    byte flags;
    float flyingSpeed, walkingSpeed;

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
        return 0x13;
    }

}
