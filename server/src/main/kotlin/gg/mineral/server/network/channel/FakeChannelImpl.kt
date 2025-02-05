package gg.mineral.server.network.channel

import gg.mineral.api.network.channel.FakeChannel
import gg.mineral.api.network.channel.MineralChannelInitializer
import gg.mineral.api.network.connection.Connection
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.embedded.EmbeddedChannel

class FakeChannelImpl(mineralChannelInitializer: MineralChannelInitializer, override var peer: FakeChannel? = null) :
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
        val peerChannel = peer as? EmbeddedChannel ?: return
        val composite = Unpooled.compositeBuffer()
        while (true) {
            val buf = this.readOutbound<ByteBuf>() ?: break
            // Append each fragment.
            composite.addComponent(true, buf)
        }
        // Reset the reader index.
        composite.readerIndex(0)
        // Write the composite as a single inbound message.
        peerChannel.writeInbound(composite)
        peerChannel.flushInbound()
    }
}