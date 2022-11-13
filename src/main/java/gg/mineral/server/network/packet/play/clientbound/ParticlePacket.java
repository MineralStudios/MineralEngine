package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class ParticlePacket implements Packet.OUTGOING {
    String particleName;
    float x, y, z, offsetX, offsetY, offsetZ, particleData;
    int numberOfParticles;

    public ParticlePacket(String particleName, float x, float y, float z, float offsetX, float offsetY, float offsetZ,
            float particleData, int numberOfParticles) {
        this.particleName = particleName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.offsetZ = offsetZ;
        this.particleData = particleData;
        this.numberOfParticles = numberOfParticles;
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, particleName);
        os.writeFloat(x);
        os.writeFloat(y);
        os.writeFloat(z);
        os.writeFloat(offsetX);
        os.writeFloat(offsetY);
        os.writeFloat(offsetZ);
        os.writeFloat(particleData);
        os.writeInt(numberOfParticles);
    }

    @Override
    public int getId() {
        return 0x2A;
    }

}
