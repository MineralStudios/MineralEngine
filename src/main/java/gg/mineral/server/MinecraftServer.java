package gg.mineral.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

import gg.mineral.server.network.Connection;
import gg.mineral.server.network.packet.AutoReadHolderHandler;
import gg.mineral.server.network.packet.PacketDecoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

public class MinecraftServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        start(25565);
    }

    static EventLoopGroup GROUP = new NioEventLoopGroup();

    /**
     * Starts the server.
     * 
     * @param port
     * 
     * @throws IOException
     * @throws InterruptedException
     */

    public static void start(int port)
            throws IOException, InterruptedException {
        ServerBootstrap b = new ServerBootstrap();
        b.group(GROUP)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.config().setTcpNoDelay(true);
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("read_timeout",
                                new ReadTimeoutHandler(3000,
                                        TimeUnit.MILLISECONDS))
                                .addLast("decoder", new PacketDecoder())
                                .addLast("flow_handler", new AutoReadHolderHandler());

                        Connection connection = new Connection();
                        Connection.LIST.add(connection);
                        pipeline.addLast("packet_handler", connection);
                    }
                });
        ChannelFuture f = b.bind().sync();
        try {
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }

    }

    public static void stop() throws InterruptedException {
        GROUP.shutdownGracefully().sync();
    }

    public static boolean DEBUG_MESSAGES = false;

    public static void setDebugMessages(boolean debugMessages) {
        MinecraftServer.DEBUG_MESSAGES = debugMessages;
    }
}
