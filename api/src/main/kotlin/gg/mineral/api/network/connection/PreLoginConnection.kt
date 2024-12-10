package gg.mineral.api.network.connection

interface PreLoginConnection : HandshakeConnection {
    /**
     * Get the name of the player attempting to connect.
     *
     * @return The name of the player attempting to connect.
     */
    val name: String?
}
