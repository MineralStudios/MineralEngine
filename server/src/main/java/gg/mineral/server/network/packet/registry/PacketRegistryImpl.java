package gg.mineral.server.network.packet.registry;

import java.util.concurrent.Callable;

import gg.mineral.api.network.packet.Packet;
import gg.mineral.api.network.packet.registry.PacketRegistry;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import lombok.val;

public class PacketRegistryImpl<P extends Packet> extends Byte2ObjectOpenHashMap<Callable<P>>
        implements PacketRegistry<P> {
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