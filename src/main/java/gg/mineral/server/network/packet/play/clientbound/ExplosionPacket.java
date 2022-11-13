package gg.mineral.server.network.packet.play.clientbound;

import java.util.List;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.world.explosion.Record;
import io.netty.buffer.ByteBuf;

public class ExplosionPacket implements Packet.OUTGOING {
    float x, y, z, radius, playerMotionX, playerMotionY, playerMotionZ;
    List<Record> records;

    public ExplosionPacket(float x, float y, float z, float radius, List<Record> records, float playerMotionX,
            float playerMotionY, float playerMotionZ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.radius = radius;
        this.records = records;
        this.playerMotionX = playerMotionX;
        this.playerMotionY = playerMotionY;
        this.playerMotionZ = playerMotionZ;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeFloat(x);
        os.writeFloat(y);
        os.writeFloat(z);
        os.writeFloat(radius);
        os.writeInt(records.size());

        for (Record record : records) {
            os.writeByte(record.getX());
            os.writeByte(record.getY());
            os.writeByte(record.getZ());
        }

        os.writeFloat(playerMotionX);
        os.writeFloat(playerMotionY);
        os.writeFloat(playerMotionZ);

    }

    @Override
    public int getId() {
        return 0x27;
    }

}
