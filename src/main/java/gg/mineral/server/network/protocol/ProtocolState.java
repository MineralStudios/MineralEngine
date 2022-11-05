package gg.mineral.server.network.protocol;

import gg.mineral.server.network.packet.handshake.serverbound.HandshakePacket;
import gg.mineral.server.network.packet.login.serverbound.EncryptionKeyResponsePacket;
import gg.mineral.server.network.packet.login.serverbound.LoginStartPacket;
import gg.mineral.server.network.packet.registry.IncomingPacketRegistry;
import gg.mineral.server.network.packet.status.bidirectional.PingPacket;
import gg.mineral.server.network.packet.status.serverbound.RequestPacket;
import io.netty.util.AttributeKey;

public class ProtocolState {
    public static final AttributeKey<IncomingPacketRegistry> ATTRIBUTE_KEY = AttributeKey
            .valueOf("protocol_state");

    public static final IncomingPacketRegistry HANDSHAKE = new IncomingPacketRegistry() {

        @Override
        public void registerPackets() {
            put(0x00, HandshakePacket.class);
        }

    };

    public static final IncomingPacketRegistry LOGIN = new IncomingPacketRegistry() {

        @Override
        public void registerPackets() {
            put(0x00, LoginStartPacket.class);
            put(0x01, EncryptionKeyResponsePacket.class);
        }

    };

    public static final IncomingPacketRegistry PLAY = new IncomingPacketRegistry() {

        @Override
        public void registerPackets() {
            // TODO Auto-generated method stub

        }

    };

    public static final IncomingPacketRegistry STATUS = new IncomingPacketRegistry() {

        @Override
        public void registerPackets() {
            put(0x00, RequestPacket.class);
            put(0x01, PingPacket.class);
        }

    };

    public static IncomingPacketRegistry getState(int i) {
        return i == 1 ? STATUS : LOGIN;
    }
}
