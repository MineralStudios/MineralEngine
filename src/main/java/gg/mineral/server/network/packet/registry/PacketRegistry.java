package gg.mineral.server.network.packet.registry;

import java.util.concurrent.Callable;

import gg.mineral.server.network.packet.Packet;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import lombok.val;

public class PacketRegistry<P extends Packet> extends Byte2ObjectOpenHashMap<Callable<P>> {
    public P create(byte id) {
        val packetBuilder = this.get(id);

        if (packetBuilder == null)
            throw new IllegalArgumentException("Unknown packet id: " + id);

        try {
            return packetBuilder.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}