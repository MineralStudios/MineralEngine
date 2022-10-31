package gg.mineral.server.packet.impl;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.chat.type.ChatColor;
import dev.zerite.craftlib.protocol.Packet;
import dev.zerite.craftlib.protocol.connection.NettyConnection;
import dev.zerite.craftlib.protocol.data.other.StatusModInfo;
import dev.zerite.craftlib.protocol.data.other.StatusPlayers;
import dev.zerite.craftlib.protocol.data.other.StatusResponse;
import dev.zerite.craftlib.protocol.data.other.StatusVersion;
import dev.zerite.craftlib.protocol.packet.status.client.ClientStatusPingPacket;
import dev.zerite.craftlib.protocol.packet.status.client.ClientStatusRequestPacket;
import dev.zerite.craftlib.protocol.packet.status.server.ServerStatusPingPacket;
import dev.zerite.craftlib.protocol.packet.status.server.ServerStatusResponsePacket;
import dev.zerite.craftlib.protocol.version.ProtocolVersion;
import gg.mineral.server.packet.IStatusPacketHandler;

public class StatusPacketHandler implements IStatusPacketHandler {

    @Override
    public void handle(NettyConnection connection, ClientStatusRequestPacket packet) {
        StatusResponse statusResponse = new StatusResponse();
        statusResponse.setFavicon("icon.png");
        statusResponse.setPlayers(new StatusPlayers(1000, 0));
        statusResponse.setModInfo(new StatusModInfo("Vanilla"));
        statusResponse.setVersion(new StatusVersion("Custom Minecraft Server", ProtocolVersion.MC1_7_6));
        BaseChatComponent chatComponent = new BaseChatComponent() {

            @Override
            protected String getLocalUnformattedText() {
                return "Hi";
            }

        };

        chatComponent.setColor(ChatColor.WHITE);

        statusResponse.setDescription(chatComponent);
        connection.send(new ServerStatusResponsePacket(statusResponse));
        // TODO Custom response
    }

    @Override
    public void handle(NettyConnection connection, ClientStatusPingPacket packet) {
        connection.send(new ServerStatusPingPacket(packet.getTime()));
    }

    @Override
    public void handle(NettyConnection connection, Packet packet) {
        if (packet instanceof ClientStatusPingPacket) {
            handle(connection, (ClientStatusPingPacket) packet);
        } else if (packet instanceof ClientStatusRequestPacket) {
            handle(connection, (ClientStatusRequestPacket) packet);
        }
    }

}
