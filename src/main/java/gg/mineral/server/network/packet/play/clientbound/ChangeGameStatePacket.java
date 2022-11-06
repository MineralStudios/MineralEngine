package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class ChangeGameStatePacket implements Packet.OUTGOING {
    short reason;
    float value;

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(reason);
        os.writeFloat(value);
    }

    @Override
    public int getId() {
        return 0x2B;
    }

}
