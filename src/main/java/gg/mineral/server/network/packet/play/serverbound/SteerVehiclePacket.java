package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class SteerVehiclePacket implements Packet.INCOMING {
    float sideways, forward;
    boolean jump, unmount;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        sideways = is.readFloat();
        forward = is.readFloat();
        jump = is.readBoolean();
        unmount = is.readBoolean();
    }
}
