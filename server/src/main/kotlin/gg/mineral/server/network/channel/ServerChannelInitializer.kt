package gg.mineral.server.network.channel

import gg.mineral.api.network.channel.MineralChannelInitializer
import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.network.packet.handler.*
import io.netty.channel.Channel
import io.netty.channel.ChannelOption
import io.netty.channel.socket.SocketChannel
import io.netty.handler.flush.FlushConsolidationHandler
import io.netty.handler.timeout.ReadTimeoutHandler


class ServerChannelInitializer(private val server: MinecraftServerImpl) : MineralChannelInitializer() {
    @Throws(Exception::class)
    override fun initChannel(channel: Channel) {
        if (channel is SocketChannel) {
            val config = channel.config()
            config.setTcpNoDelay(true)
            config.setOption(ChannelOption.IP_TOS, 0x18)
        }

        val pipeline = channel.pipeline()
        pipeline.addFirst(FlushConsolidationHandler())
        pipeline
            .addLast("timeout", ReadTimeoutHandler(30))
            .addLast("decoder", PacketDecoder())
            .addLast("prepender", PacketPrepender.INSTANCE)
            .addLast("encoder", PacketEncoder())
            .addLast("flow_handler", AutoReadHolderHandler())
            .addLast("packet_handler", server.newConnection())
    }
}
