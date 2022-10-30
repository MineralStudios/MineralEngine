package gg.mineral.server.packet;

import dev.zerite.craftlib.protocol.Packet;
import dev.zerite.craftlib.protocol.connection.NettyConnection;
import dev.zerite.craftlib.protocol.packet.play.client.display.ClientPlayAnimationPacket;
import dev.zerite.craftlib.protocol.packet.play.client.display.ClientPlayChatMessagePacket;
import dev.zerite.craftlib.protocol.packet.play.client.interaction.ClientPlayEntityActionPacket;
import dev.zerite.craftlib.protocol.packet.play.client.interaction.ClientPlayPlayerBlockPlacementPacket;
import dev.zerite.craftlib.protocol.packet.play.client.interaction.ClientPlayPlayerDiggingPacket;
import dev.zerite.craftlib.protocol.packet.play.client.interaction.ClientPlayUseEntityPacket;
import dev.zerite.craftlib.protocol.packet.play.client.inventory.ClientPlayClickWindowPacket;
import dev.zerite.craftlib.protocol.packet.play.client.inventory.ClientPlayCloseWindowPacket;
import dev.zerite.craftlib.protocol.packet.play.client.inventory.ClientPlayConfirmTransactionPacket;
import dev.zerite.craftlib.protocol.packet.play.client.inventory.ClientPlayCreativeInventoryActionPacket;
import dev.zerite.craftlib.protocol.packet.play.client.inventory.ClientPlayEnchantItemPacket;
import dev.zerite.craftlib.protocol.packet.play.client.other.ClientPlayClientSettingsPacket;
import dev.zerite.craftlib.protocol.packet.play.client.other.ClientPlayClientStatusPacket;
import dev.zerite.craftlib.protocol.packet.play.client.other.ClientPlayKeepAlivePacket;
import dev.zerite.craftlib.protocol.packet.play.client.other.ClientPlayPluginMessagePacket;
import dev.zerite.craftlib.protocol.packet.play.client.other.ClientPlayTabCompletePacket;
import dev.zerite.craftlib.protocol.packet.play.client.other.ClientPlayUpdateSignPacket;
import dev.zerite.craftlib.protocol.packet.play.client.player.ClientPlayPlayerAbilitiesPacket;
import dev.zerite.craftlib.protocol.packet.play.client.player.ClientPlayPlayerHeldItemChangePacket;
import dev.zerite.craftlib.protocol.packet.play.client.player.ClientPlayPlayerLookPacket;
import dev.zerite.craftlib.protocol.packet.play.client.player.ClientPlayPlayerPacket;
import dev.zerite.craftlib.protocol.packet.play.client.player.ClientPlayPlayerPositionLookPacket;
import dev.zerite.craftlib.protocol.packet.play.client.player.ClientPlayPlayerPositionPacket;
import dev.zerite.craftlib.protocol.packet.play.client.player.ClientPlayPlayerSteerVehiclePacket;

public interface IPlayPacketHandler {

    public void handle(NettyConnection connection, Packet packet);

    public void handle(NettyConnection connection, ClientPlayKeepAlivePacket packet);

    public void handle(NettyConnection connection, ClientPlayChatMessagePacket packet);

    public void handle(NettyConnection connection, ClientPlayUseEntityPacket packet);

    public void handle(NettyConnection connection, ClientPlayPlayerPacket packet);

    public void handle(NettyConnection connection, ClientPlayPlayerPositionPacket packet);

    public void handle(NettyConnection connection, ClientPlayPlayerLookPacket packet);

    public void handle(NettyConnection connection, ClientPlayPlayerPositionLookPacket packet);

    public void handle(NettyConnection connection, ClientPlayPlayerDiggingPacket packet);

    public void handle(NettyConnection connection, ClientPlayPlayerBlockPlacementPacket packet);

    public void handle(NettyConnection connection, ClientPlayPlayerHeldItemChangePacket packet);

    public void handle(NettyConnection connection, ClientPlayAnimationPacket packet);

    public void handle(NettyConnection connection, ClientPlayEntityActionPacket packet);

    public void handle(NettyConnection connection, ClientPlayPlayerSteerVehiclePacket packet);

    public void handle(NettyConnection connection, ClientPlayCloseWindowPacket packet);

    public void handle(NettyConnection connection, ClientPlayClickWindowPacket packet);

    public void handle(NettyConnection connection, ClientPlayConfirmTransactionPacket packet);

    public void handle(NettyConnection connection, ClientPlayCreativeInventoryActionPacket packet);

    public void handle(NettyConnection connection, ClientPlayEnchantItemPacket packet);

    public void handle(NettyConnection connection, ClientPlayUpdateSignPacket packet);

    public void handle(NettyConnection connection, ClientPlayPlayerAbilitiesPacket packet);

    public void handle(NettyConnection connection, ClientPlayTabCompletePacket packet);

    public void handle(NettyConnection connection, ClientPlayClientSettingsPacket packet);

    public void handle(NettyConnection connection, ClientPlayClientStatusPacket packet);

    public void handle(NettyConnection connection, ClientPlayPluginMessagePacket packet);
}
