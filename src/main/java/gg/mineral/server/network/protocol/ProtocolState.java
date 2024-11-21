package gg.mineral.server.network.protocol;

import gg.mineral.server.network.packet.Packet.INCOMING;
import gg.mineral.server.network.packet.handshake.serverbound.HandshakePacket;
import gg.mineral.server.network.packet.login.serverbound.EncryptionKeyResponsePacket;
import gg.mineral.server.network.packet.login.serverbound.LoginStartPacket;
import gg.mineral.server.network.packet.play.bidirectional.AnimationPacket;
import gg.mineral.server.network.packet.play.bidirectional.CloseWindowPacket;
import gg.mineral.server.network.packet.play.bidirectional.ConfirmTransactionPacket;
import gg.mineral.server.network.packet.play.bidirectional.HeldItemChangePacket;
import gg.mineral.server.network.packet.play.bidirectional.KeepAlivePacket;
import gg.mineral.server.network.packet.play.bidirectional.PlayerAbilitiesPacket;
import gg.mineral.server.network.packet.play.bidirectional.PluginMessagePacket;
import gg.mineral.server.network.packet.play.bidirectional.UpdateSignPacket;
import gg.mineral.server.network.packet.play.serverbound.ChatMessagePacket;
import gg.mineral.server.network.packet.play.serverbound.ClickWindowPacket;
import gg.mineral.server.network.packet.play.serverbound.ClientSettingsPacket;
import gg.mineral.server.network.packet.play.serverbound.ClientStatusPacket;
import gg.mineral.server.network.packet.play.serverbound.CreativeInventoryActionPacket;
import gg.mineral.server.network.packet.play.serverbound.EnchantItemPacket;
import gg.mineral.server.network.packet.play.serverbound.EntityActionPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerBlockPlacementPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerDiggingPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerLookPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerPositionAndLookPacket;
import gg.mineral.server.network.packet.play.serverbound.PlayerPositionPacket;
import gg.mineral.server.network.packet.play.serverbound.SteerVehiclePacket;
import gg.mineral.server.network.packet.play.serverbound.TabCompletePacket;
import gg.mineral.server.network.packet.play.serverbound.UseEntityPacket;
import gg.mineral.server.network.packet.registry.PacketRegistry;
import gg.mineral.server.network.packet.status.bidirectional.PingPacket;
import gg.mineral.server.network.packet.status.serverbound.RequestPacket;
import io.netty.util.AttributeKey;

public class ProtocolState {
    public static final AttributeKey<PacketRegistry<INCOMING>> ATTRIBUTE_KEY = AttributeKey
            .valueOf("protocol_state");

    public static final PacketRegistry<INCOMING> HANDSHAKE = new PacketRegistry<>() {
        {
            put((byte) 0x00, HandshakePacket::new);
        }
    };

    public static final PacketRegistry<INCOMING> LOGIN = new PacketRegistry<>() {
        {
            put((byte) 0x00, LoginStartPacket::new);
            put((byte) 0x01, EncryptionKeyResponsePacket::new);
        }
    };

    public static final PacketRegistry<INCOMING> PLAY = new PacketRegistry<>() {
        {
            put((byte) 0x0A, AnimationPacket::new);
            put((byte) 0x01, ChatMessagePacket::new);
            put((byte) 0x09, HeldItemChangePacket::new);
            put((byte) 0x00, KeepAlivePacket::new);
            put((byte) 0x0E, ClickWindowPacket::new);
            put((byte) 0x15, ClientSettingsPacket::new);
            put((byte) 0x16, ClientStatusPacket::new);
            put((byte) 0x0D, CloseWindowPacket::new);
            put((byte) 0x0F, ConfirmTransactionPacket::new);
            put((byte) 0x10, CreativeInventoryActionPacket::new);
            put((byte) 0x11, EnchantItemPacket::new);
            put((byte) 0x0B, EntityActionPacket::new);
            put((byte) 0x13, PlayerAbilitiesPacket::new);
            put((byte) 0x08, PlayerBlockPlacementPacket::new);
            put((byte) 0x07, PlayerDiggingPacket::new);
            put((byte) 0x05, PlayerLookPacket::new);
            put((byte) 0x03, PlayerPacket::new);
            put((byte) 0x06, PlayerPositionAndLookPacket::new);
            put((byte) 0x04, PlayerPositionPacket::new);
            put((byte) 0x17, PluginMessagePacket::new);
            put((byte) 0x0C, SteerVehiclePacket::new);
            put((byte) 0x14, TabCompletePacket::new);
            put((byte) 0x12, UpdateSignPacket::new);
            put((byte) 0x02, UseEntityPacket::new);
        }
    };

    public static final PacketRegistry<INCOMING> STATUS = new PacketRegistry<>() {
        {
            put((byte) 0x00, RequestPacket::new);
            put((byte) 0x01, PingPacket::new);
        }
    };

    public static PacketRegistry<INCOMING> getState(int i) {
        return i == 1 ? STATUS : LOGIN;
    }
}
