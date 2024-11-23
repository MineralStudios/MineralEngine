package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.world.explosion.ExplosionRecord;
import io.netty.buffer.ByteBuf;
import lombok.val;

public record ExplosionPacket(float x, float y, float z, float radius, List<ExplosionRecord> records,
        float playerMotionX,
        float playerMotionY, float playerMotionZ) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeFloat(x);
        os.writeFloat(y);
        os.writeFloat(z);
        os.writeFloat(radius);
        os.writeInt(records.size());

        for (val record : records) {
            os.writeByte(record.x());
            os.writeByte(record.y());
            os.writeByte(record.z());
        }

        os.writeFloat(playerMotionX);
        os.writeFloat(playerMotionY);
        os.writeFloat(playerMotionZ);
    }

    @Override
    public byte getId() {
        return 0x27;
    }

}
