package gg.mineral.server.packet.impl;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.chat.type.ChatColor;
import dev.zerite.craftlib.protocol.Packet;
import dev.zerite.craftlib.protocol.connection.NettyConnection;
import dev.zerite.craftlib.protocol.data.registry.impl.MagicChatPosition;
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
import dev.zerite.craftlib.protocol.packet.play.server.display.ServerPlayChatMessagePacket;
import dev.zerite.craftlib.protocol.packet.play.server.other.ServerPlayKeepAlivePacket;
import gg.mineral.server.command.Command;
import gg.mineral.server.packet.IPlayPacketHandler;

public class PlayPacketHandler implements IPlayPacketHandler {

    public void handle(NettyConnection connection, ClientPlayKeepAlivePacket packet) {
        connection.send(new ServerPlayKeepAlivePacket(packet.getId()));
    }

    public void handle(NettyConnection connection, ClientPlayChatMessagePacket packet) {
        String message = packet.getMessage();
        boolean isCommand = message.startsWith("/");

        if (isCommand) {
            String[] split = message.split(",");
            String commandName = split[0];
            Command command = Command.byName(commandName);

            if (command == null) {
                // TODO Better message
                BaseChatComponent chatComponent = new BaseChatComponent() {

                    @Override
                    protected String getLocalUnformattedText() {
                        return ChatColor.AQUA + "Invalid command.";
                    }

                };

                connection.send(new ServerPlayChatMessagePacket(chatComponent, MagicChatPosition.CHAT));
                return;
            }

            // TODO execute Command
        }

        // TODO send message

    }

    @Override
    public void handle(NettyConnection connection, Packet packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayUseEntityPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayPlayerPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayPlayerPositionPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayPlayerLookPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayPlayerPositionLookPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayPlayerDiggingPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayPlayerBlockPlacementPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayPlayerHeldItemChangePacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayAnimationPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayEntityActionPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayPlayerSteerVehiclePacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayCloseWindowPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayClickWindowPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayConfirmTransactionPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayCreativeInventoryActionPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayEnchantItemPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayUpdateSignPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayPlayerAbilitiesPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayTabCompletePacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayClientSettingsPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayClientStatusPacket packet) {
        // TODO Auto-generated method stub

    }

    @Override
    public void handle(NettyConnection connection, ClientPlayPluginMessagePacket packet) {
        // TODO Auto-generated method stub

    }

}
