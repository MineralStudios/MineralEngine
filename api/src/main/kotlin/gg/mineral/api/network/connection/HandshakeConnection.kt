package gg.mineral.api.network.connection

interface HandshakeConnection : IConnection {
    /**
     * Get the IP address of the connection.
     *
     * @return The IP address of the connection.
     */
    val ipAddress: String?
}
