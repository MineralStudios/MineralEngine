package gg.mineral.server.network.channel

import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.network.connection.ConnectionImpl
import gg.mineral.server.network.packet.handler.AutoReadHolderHandler
import gg.mineral.server.network.packet.handler.PacketDecoder
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

class MineralChannelInitializer(private val server: MinecraftServerImpl) : ChannelInitializer<SocketChannel>() {
    @Throws(Exception::class)
    override fun initChannel(socketChannel: SocketChannel) {
        socketChannel.config().setTcpNoDelay(true)
        val pipeline = socketChannel.pipeline()
        pipeline.addLast("decoder", PacketDecoder())
            .addLast("flow_handler", AutoReadHolderHandler())

        val connection = ConnectionImpl(server)

        synchronized(server.connections) {
            server.connections.add(connection)
        }

        pipeline.addLast("packet_handler", connection)
    }
}
