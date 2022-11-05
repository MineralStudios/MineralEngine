package gg.mineral.server.network.packet.registry;

import java.util.concurrent.Callable;

import gg.mineral.server.network.packet.Packet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public abstract class IncomingPacketRegistry {

    public IncomingPacketRegistry() {
        registerPackets();
    }

    final Int2ObjectOpenHashMap<Callable<Packet.INCOMING>> INCOMING = new Int2ObjectOpenHashMap<>();

    public void put(int id, Callable<Packet.INCOMING> packetBuilder) {
        INCOMING.put(id, packetBuilder);
    }

    public Callable<Packet.INCOMING> get(int id) {
        return INCOMING.get(id);
    }

    public abstract void registerPackets();

}