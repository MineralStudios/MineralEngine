package gg.mineral.server.network.packet.registry

import gg.mineral.api.network.packet.Packet
import gg.mineral.api.network.packet.registry.PacketRegistry
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap
import java.util.concurrent.Callable

open class PacketRegistryImpl<P : Packet> : PacketRegistry<P> {
    private val underlyingMap: Byte2ObjectOpenHashMap<Callable<P>> = Byte2ObjectOpenHashMap()

    override fun put(key: Byte, value: Callable<P>): Callable<P>? = underlyingMap.put(key, value)

    override fun create(id: Byte): P {
        val packetBuilder: Callable<P>? = underlyingMap[id]

        requireNotNull(packetBuilder) { "Unknown packet id: $id" }

        try {
            return packetBuilder.call()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}