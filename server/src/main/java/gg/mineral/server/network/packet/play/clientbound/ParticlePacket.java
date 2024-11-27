package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.network.packet.Packet;

import io.netty.buffer.ByteBuf;

public final record ParticlePacket(String particleName, float x, float y, float z, float offsetX, float offsetY,
        float offsetZ, float particleData, int numberOfParticles) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        writeString(os, particleName);
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
    public byte getId() {
        return 0x2A;
    }
}
