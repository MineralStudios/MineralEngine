package gg.mineral.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.handler.AutoReadHolderHandler;
import gg.mineral.server.network.packet.handler.PacketDecoder;
import gg.mineral.server.tick.TickLoop;
import gg.mineral.server.tick.TickThreadFactory;
import gg.mineral.server.world.WorldManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import java.util.concurrent.ScheduledExecutorService;
import lombok.Getter;

public class MinecraftServer {

    @Getter
    static final TickLoop tickLoop = new TickLoop();

    @Getter
    static final ScheduledExecutorService tickExecutor = Executors.newScheduledThreadPool(getTickThreadCount(),
            TickThreadFactory.INSTANCE);

    public static int getTickThreadCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public static int getNetworkThreadCount() {
        return getTickThreadCount() / 2;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        start(25565);
    }

    static EventLoopGroup GROUP = new NioEventLoopGroup(getNetworkThreadCount());

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
        WorldManager.init();
        ServerBootstrap b = new ServerBootstrap();
        b.group(GROUP)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.config().setTcpNoDelay(true);
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast("decoder", new PacketDecoder())
                                .addLast("flow_handler", new AutoReadHolderHandler());

                        Connection connection = new Connection();
                        Connection.LIST.add(connection);
                        pipeline.addLast("packet_handler", connection);
                    }
                });
        ChannelFuture f = b.bind().sync();

        System.out.println("[Mineral] Server started on port " + port);
        tickLoop.start();
        try {
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }

    }

    public static void stop() throws InterruptedException {
        tickExecutor.shutdown();
        GROUP.shutdownGracefully().sync();
    }

    public static boolean DEBUG_MESSAGES = false;

    public static void setDebugMessages(boolean debugMessages) {
        MinecraftServer.DEBUG_MESSAGES = debugMessages;
    }
}
