package gg.mineral.server.network.channel

import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.network.connection.ConnectionImpl
import gg.mineral.server.network.packet.handler.*
import io.netty.channel.ChannelInitializer
import io.netty.channel.ChannelOption
import io.netty.channel.socket.SocketChannel
import io.netty.handler.flush.FlushConsolidationHandler
import io.netty.handler.timeout.ReadTimeoutHandler


class MineralChannelInitializer(private val server: MinecraftServerImpl) : ChannelInitializer<SocketChannel>() {
    @Throws(Exception::class)
    override fun initChannel(socketChannel: SocketChannel) {
        val config = socketChannel.config()
        config.setTcpNoDelay(true)
        config.setOption(ChannelOption.IP_TOS, 0x18);

        val pipeline = socketChannel.pipeline()
        pipeline.addFirst(FlushConsolidationHandler())
        pipeline
            .addLast("timeout", ReadTimeoutHandler(30))
            .addLast("decoder", PacketDecoder())
            .addLast("prepender", PacketPrepender.INSTANCE)
            .addLast("encoder", PacketEncoder())
            .addLast("flow_handler", AutoReadHolderHandler())

        val connection = ConnectionImpl(server)

        synchronized(server.connections) {
            server.connections.add(connection)
        }

        pipeline.addLast("packet_handler", connection)
    }
}
