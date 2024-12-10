package gg.mineral.api.network.packet.registry

import gg.mineral.api.network.packet.Packet
import java.util.concurrent.Callable

interface PacketRegistry<P : Packet> {
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
     * @param key
     * @param value
     * @return The previous packet builder.
     */
    fun put(key: Byte, value: Callable<P>): Callable<P>?
}
