package gg.mineral.api.network.connection;

public interface HandshakeConnection extends IConnection {
    /**
     * Get the IP address of the connection.
     * 
     * @return The IP address of the connection.
     */
    String getIpAddress();

}
