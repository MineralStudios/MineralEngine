package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class ConfirmTransactionPacket implements Packet.OUTGOING {
    short windowId, actionNumber;
    boolean accepted;

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(windowId);
        os.writeShort(actionNumber);
        os.writeBoolean(accepted);
    }

    @Override
    public int getId() {
        return 0x32;
    }

}
