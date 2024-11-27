package gg.mineral.api;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import gg.mineral.api.command.CommandMap;
import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.tick.TickLoop;

import gg.mineral.api.entity.living.human.Player;

public interface MinecraftServer {
    /**
     * Gets the map of registered commands.
     * 
     * @return The map of registered commands.
     */
    CommandMap getRegisteredCommands();

    /**
     * Gets the tick loop.
     * 
     * @return The tick loop.
     */
    TickLoop getTickLoop();

    /**
     * Gets if debug messages are enabled.
     * 
     * @return If debug messages are enabled.
     */
    boolean isDebugMessages();

    /**
     * Sets if debug messages are enabled.
     * 
     * @param debugMessages If debug messages are enabled.
     */
    void setDebugMessages(boolean debugMessages);

    /**
     * Gets the connections.
     * 
     * @return The connections.
     */
    Set<Connection> getConnections();

    /**
     * Gets the tick executor.
     * 
     * @return The tick executor.
     */
    ScheduledExecutorService getTickExecutor();

    /**
     * Gets the async executor.
     * 
     * @return The async executor.
     */
    ExecutorService getAsyncExecutor();

    /**
     * Starts the server.
     * 
     * @param port The port to start the server on.
     * 
     * @throws IllegalArgumentException If the port is invalid.
     * 
     * @throws IllegalStateException    If the server is already running.
     */
    void start(int port);

    /**
     * Stops the server.
     */
    void stop();

    /**
     * Gets all the online players.
     * 
     * @return All the online players.
     */
    Collection<Player> getOnlinePlayers();

    /**
     * Gets the player with the specified name.
     * 
     * @param name The name of the player.
     * 
     * @return The player with the specified name.
     */
    Player getPlayer(String name);

    /**
     * Gets the player with the specified entity ID.
     * 
     * @param entityId The entity ID of the player.
     * 
     * @return The player with the specified entity ID.
     */
    Player getPlayer(int entityId);
}