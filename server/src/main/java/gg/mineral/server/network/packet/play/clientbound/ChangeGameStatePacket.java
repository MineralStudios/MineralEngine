package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public final record ChangeGameStatePacket(short reason, float value) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(reason);
        os.writeFloat(value);
    }

    @Override
    public byte getId() {
        return 0x2B;
    }
}
