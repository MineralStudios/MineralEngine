package gg.mineral.api.network.packet.registry;

import java.util.concurrent.Callable;

import gg.mineral.api.network.packet.Packet;

public interface PacketRegistry<P extends Packet> {
    /**
     * Creates a packet with the given id.
     * 
     * @param id
     * @return The packet.
     */
    P create(byte id);

    /**
     * Puts a packet builder into the registry.
     * 
     * @param id
     * @param packetBuilder
     * @return The previous packet builder.
     */
    Callable<P> put(byte id, Callable<P> packetBuilder);
}
