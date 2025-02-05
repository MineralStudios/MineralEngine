package gg.mineral.server.network.channel

import gg.mineral.api.network.channel.FakeChannel
import gg.mineral.api.network.channel.MineralChannelInitializer
import gg.mineral.api.network.connection.Connection
import io.netty.channel.Channel
import io.netty.channel.embedded.EmbeddedChannel

class FakeChannelImpl(mineralChannelInitializer: MineralChannelInitializer, var peer: FakeChannelImpl? = null) :
    EmbeddedChannel(),
    FakeChannel {
    override val connection: Connection
        get() = this.pipeline().get("packet_handler") as Connection

    init {
        mineralChannelInitializer.initChannel(this)
        this.pipeline().fireChannelActive()
    }

    override fun flush(): Channel {
        val value = super.flush()
        if (peer != null) transferToPeer()
        return value
    }

    private fun transferToPeer() {
        while (true) {
            val outbound = this.readOutbound<Any>() ?: break
            peer?.writeInbound(outbound)
        }
    }
}