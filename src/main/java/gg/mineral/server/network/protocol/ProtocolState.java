package gg.mineral.server.network.protocol;

import gg.mineral.server.network.packet.handshake.serverbound.HandshakePacket;
import gg.mineral.server.network.packet.login.serverbound.EncryptionKeyResponsePacket;
import gg.mineral.server.network.packet.login.serverbound.LoginStartPacket;
import gg.mineral.server.network.packet.play.bidirectional.HeldItemChangePacket;
import gg.mineral.server.network.packet.play.bidirectional.KeepAlivePacket;
import gg.mineral.server.network.packet.play.serverbound.AnimationPacket;
import gg.mineral.server.network.packet.play.serverbound.ChatMessagePacket;
import gg.mineral.server.network.packet.play.serverbound.ClickWindowPacket;
import gg.mineral.server.network.packet.play.serverbound.ClientSettingsPacket;
import gg.mineral.server.network.packet.play.serverbound.ClientStatusPacket;
import gg.mineral.server.network.packet.play.serverbound.CloseWindowPacket;
import gg.mineral.server.network.packet.play.serverbound.ConfirmTransactionPacket;
import gg.mineral.server.network.packet.play.serverbound.CreativeInventoryActionPacket;
import gg.mineral.server.network.packet.play.serverbound.EnchantItemPacket;
import gg.mineral.server.network.packet.play.serverbound.EntityActionPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerAbilitiesPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerBlockPlacementPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerDiggingPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerLookPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerPositionAndLookPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerPositionPacket;
import gg.mineral.server.network.packet.play.serverbound.PluginMessagePacket;
import gg.mineral.server.network.packet.play.serverbound.SteerVehiclePacket;
import gg.mineral.server.network.packet.play.serverbound.TabCompletePacket;
import gg.mineral.server.network.packet.play.serverbound.UpdateSignPacket;
import gg.mineral.server.network.packet.play.serverbound.UseEntityPacket;
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
            put(0x00, () -> new HandshakePacket());
        }

    };

    public static final IncomingPacketRegistry LOGIN = new IncomingPacketRegistry() {

        @Override
        public void registerPackets() {
            put(0x00, () -> new LoginStartPacket());
            put(0x01, () -> new EncryptionKeyResponsePacket());
        }

    };

    public static final IncomingPacketRegistry PLAY = new IncomingPacketRegistry() {

        @Override
        public void registerPackets() {
            put(0x0A, () -> new AnimationPacket());
            put(0x01, () -> new ChatMessagePacket());
            put(0x09, () -> new HeldItemChangePacket());
            put(0x00, () -> new KeepAlivePacket());
            put(0x0E, () -> new ClickWindowPacket());
            put(0x15, () -> new ClientSettingsPacket());
            put(0x16, () -> new ClientStatusPacket());
            put(0x0D, () -> new CloseWindowPacket());
            put(0x0F, () -> new ConfirmTransactionPacket());
            put(0x10, () -> new CreativeInventoryActionPacket());
            put(0x11, () -> new EnchantItemPacket());
            put(0x0B, () -> new EntityActionPacket());
            put(0x13, () -> new PlayerAbilitiesPacket());
            put(0x08, () -> new PlayerBlockPlacementPacket());
            put(0x07, () -> new PlayerDiggingPacket());
            put(0x05, () -> new PlayerLookPacket());
            put(0x03, () -> new PlayerPacket());
            put(0x06, () -> new PlayerPositionAndLookPacket());
            put(0x04, () -> new PlayerPositionPacket());
            put(0x17, () -> new PluginMessagePacket());
            put(0x0C, () -> new SteerVehiclePacket());
            put(0x14, () -> new TabCompletePacket());
            put(0x12, () -> new UpdateSignPacket());
            put(0x02, () -> new UseEntityPacket());
        }

    };

    public static final IncomingPacketRegistry STATUS = new IncomingPacketRegistry() {

        @Override
        public void registerPackets() {
            put(0x00, () -> new RequestPacket());
            put(0x01, () -> new PingPacket());
        }

    };

    public static IncomingPacketRegistry getState(int i) {
        return i == 1 ? STATUS : LOGIN;
    }
}
