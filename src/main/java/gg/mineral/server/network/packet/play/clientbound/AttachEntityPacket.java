package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;

public class AttachEntityPacket implements Packet.OUTGOING {

    int entityId, vehicleId;
    boolean leash;

    public AttachEntityPacket(int entityId, int vehicleId, boolean leash) {
        this.entityId = entityId;
        this.vehicleId = vehicleId;
        this.leash = leash;
    }

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
