package gg.mineral.server.network.packet.registry;

import gg.mineral.server.network.packet.IncomingPacket;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public abstract class IncomingPacketRegistry {

    public IncomingPacketRegistry() {
        registerPackets();
    }

    final Int2ObjectOpenHashMap<Class<? extends IncomingPacket>> INCOMING = new Int2ObjectOpenHashMap<>();

    public void put(int id, Class<? extends IncomingPacket> packetClass) {
        INCOMING.put(id, packetClass);
    }

    public Class<? extends IncomingPacket> get(int id) {
        return INCOMING.get(id);
    }

    public abstract void registerPackets();

}