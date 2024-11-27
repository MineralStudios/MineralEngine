package gg.mineral.api.network.connection;

public interface PreLoginConnection extends HandshakeConnection {
    /**
     * Get the name of the player attempting to connect.
     * 
     * @return The name of the player attempting to connect.
     */
    String getName();
}
