package gg.mineral.api.network.channel

import gg.mineral.api.network.connection.Connection

interface FakeChannel {
    /**
     * Get the connection associated with this channel.
     *
     * @return The connection associated with this channel.
     */
    val connection: Connection
}