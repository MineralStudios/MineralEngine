package gg.mineral.api.network.connection;

import java.util.UUID;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import gg.mineral.api.MinecraftServer;
import gg.mineral.api.entity.living.human.Player;
import gg.mineral.api.network.packet.Packet;

public interface Connection extends PreLoginConnection {
    /**
     * Disconnects the connection.
     * 
     * @param disconnectMessage
     */
    void disconnect(BaseChatComponent disconnectMessage);

    /**
     * Gets the player attached to the connection.
     * 
     * @return The player attached to the connection.
     */
    Player getPlayer();

    /**
     * Gets the UUID of the player attached to the connection.
     * 
     * @return The UUID of the player attached to the connection.
     */
    UUID getUuid();

    /**
     * Gets the server.
     * 
     * @return The server.
     */
    MinecraftServer getServer();

    /**
     * Queues packets to be sent to the client.
     * 
     * @param packets
     */
    void queuePacket(Packet.OUTGOING... packets);
}
