package gg.mineral.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.jetbrains.annotations.Nullable;

import gg.mineral.server.command.Command;
import gg.mineral.server.command.impl.KnockbackCommand;
import gg.mineral.server.command.impl.TPSCommand;
import gg.mineral.server.command.impl.VersionCommand;
import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.channel.MineralChannelInitializer;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.tick.TickLoop;
import gg.mineral.server.tick.TickThreadFactory;
import gg.mineral.server.util.collection.GlueList;
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
import lombok.Setter;
import lombok.val;

@Getter
public class MinecraftServer {

    private static final int NETWORK_THREADS = Runtime.getRuntime().availableProcessors();

    @Getter
    private static final ExecutorService asyncExecutor = Executors.newWorkStealingPool();
    @Getter
    private static final ScheduledExecutorService tickExecutor = Executors
            .newSingleThreadScheduledExecutor(TickThreadFactory.INSTANCE);

    @Setter
    public boolean debugMessages = false;

    private final List<Command> registeredCommands = new GlueList<Command>() {
        {
            add(new TPSCommand());
            add(new VersionCommand());
            add(new KnockbackCommand());
        }
    };

    private final List<Connection> connections = new GlueList<>();

    @Nullable
    public Command commandByName(String name) {
        for (val command : registeredCommands)
            if (command.getName().equalsIgnoreCase(name))
                return command;

        return null;
    }

    private final TickLoop tickLoop = new TickLoop(this);

    private final EntityManager entityManager = new EntityManager();

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

}
