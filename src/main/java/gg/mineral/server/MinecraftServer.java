package gg.mineral.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import gg.mineral.server.network.channel.MineralChannelInitializer;
import gg.mineral.server.tick.TickLoop;
import gg.mineral.server.world.WorldManager;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.kqueue.KQueue;
import io.netty.channel.kqueue.KQueueEventLoopGroup;
import io.netty.channel.kqueue.KQueueServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import lombok.val;

public class MinecraftServer {

    private static final int NETWORK_THREADS = Runtime.getRuntime().availableProcessors();

    @Getter
    private final TickLoop tickLoop = new TickLoop();

    @Getter
    private static final ExecutorService asyncExecutor = Executors.newWorkStealingPool();
    @Getter
    private static final ScheduledExecutorService tickExecutor = Executors.newSingleThreadScheduledExecutor();

    public static void main(String[] args) throws IOException, InterruptedException {
        new MinecraftServer().start(25565);
    }

    private EventLoopGroup group;

    /**
     * Starts the server.
     * 
     * @param port
     * 
     * @throws IOException
     * @throws InterruptedException
     */

    public void start(int port)
            throws IOException, InterruptedException {
        WorldManager.init();
        if (Epoll.isAvailable())
            group = new EpollEventLoopGroup(NETWORK_THREADS);
        else if (KQueue.isAvailable())
            group = new KQueueEventLoopGroup(NETWORK_THREADS);
        else
            group = new NioEventLoopGroup(NETWORK_THREADS);

        val b = new ServerBootstrap();
        b.group(group)
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class
                        : KQueue.isAvailable()
                                ? KQueueServerSocketChannel.class
                                : NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new MineralChannelInitializer(this));
        val f = b.bind().sync();

        System.out.println("[Mineral] Server started on port " + port);
        tickLoop.start();
        try {
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
        }

    }

    public void stop() throws InterruptedException {
        tickExecutor.shutdown();
        asyncExecutor.shutdown();
        if (group != null)
            group.shutdownGracefully().sync();
    }

    public boolean debugMessages = false;

    public void setDebugMessages(boolean debugMessages) {
        this.debugMessages = debugMessages;
    }
}
