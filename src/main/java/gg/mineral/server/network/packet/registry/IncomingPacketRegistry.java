package gg.mineral.server.network.packet.registry;

import java.util.concurrent.Callable;

import gg.mineral.server.network.packet.Packet;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;

public abstract class IncomingPacketRegistry {

    public IncomingPacketRegistry() {
        registerPackets();
    }

    final Byte2ObjectOpenHashMap<Callable<Packet.INCOMING>> INCOMING = new Byte2ObjectOpenHashMap<>();

    public void put(byte id, Callable<Packet.INCOMING> packetBuilder) {
        INCOMING.put(id, packetBuilder);
    }

    public Callable<Packet.INCOMING> get(byte id) {
        return INCOMING.get(id);
    }

    public abstract void registerPackets();

}