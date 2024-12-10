package gg.mineral.api.network.packet.registry

import gg.mineral.api.network.packet.Packet
import java.util.concurrent.Callable

interface PacketRegistry<P : Packet?> {
    /**
     * Creates a packet with the given id.
     *
     * @param id
     * @return The packet.
     */
    fun create(id: Byte): P

    /**
     * Puts a packet builder into the registry.
     *
     * @param id
     * @param packetBuilder
     * @return The previous packet builder.
     */
    fun put(id: Byte, packetBuilder: Callable<P>?): Callable<P>?
}
