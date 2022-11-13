package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class ConfirmTransactionPacket implements Packet.INCOMING {
    byte windowId;
    short actionNumber;
    boolean accepted;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        windowId = is.readByte();
        actionNumber = is.readShort();
        accepted = is.readBoolean();
    }

    @Override
    public int getId() {
        return 0x0F;
    }

}
