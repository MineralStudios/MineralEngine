package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public record AttachEntityPacket(int entityId, int vehicleId, boolean leash) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeInt(vehicleId);
        os.writeBoolean(leash);
    }

    @Override
    public byte getId() {
        return 0x1B;
    }
}
