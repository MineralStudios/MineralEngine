package gg.mineral.server.network.channel;

import io.netty.channel.socket.SocketChannel;
import lombok.RequiredArgsConstructor;
import lombok.val;
import gg.mineral.server.MinecraftServerImpl;
import gg.mineral.server.network.connection.ConnectionImpl;
import gg.mineral.server.network.packet.handler.AutoReadHolderHandler;
import gg.mineral.server.network.packet.handler.PacketDecoder;
import io.netty.channel.ChannelInitializer;

@RequiredArgsConstructor
public class MineralChannelInitializer extends ChannelInitializer<SocketChannel> {
    private final MinecraftServerImpl server;

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.config().setTcpNoDelay(true);
        val pipeline = socketChannel.pipeline();
        pipeline.addLast("decoder", new PacketDecoder())
                .addLast("flow_handler", new AutoReadHolderHandler());

        val connection = new ConnectionImpl(server);

        synchronized (server.getConnections()) {
            server.getConnections().add(connection);
        }

        pipeline.addLast("packet_handler", connection);
    }

}
