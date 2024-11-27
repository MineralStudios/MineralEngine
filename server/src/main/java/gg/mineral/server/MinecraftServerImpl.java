package gg.mineral.server;

import java.io.File;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import gg.mineral.api.MinecraftServer;
import gg.mineral.api.entity.living.human.Player;
import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.world.World;
import gg.mineral.api.world.World.Environment;
import gg.mineral.api.world.World.Generator;
import gg.mineral.server.command.CommandMapImpl;
import gg.mineral.server.command.impl.KnockbackCommand;
import gg.mineral.server.command.impl.TPSCommand;
import gg.mineral.server.command.impl.VersionCommand;
import gg.mineral.server.entity.EntityImpl;
import gg.mineral.server.entity.living.human.PlayerImpl;
import gg.mineral.server.network.channel.MineralChannelInitializer;
import gg.mineral.server.network.connection.ConnectionImpl;
import gg.mineral.server.tick.TickLoopImpl;
import gg.mineral.server.tick.TickThreadFactory;
import gg.mineral.server.util.messages.Messages;
import gg.mineral.server.world.WorldImpl;
import gg.mineral.server.world.chunk.ChunkImpl;
import gg.mineral.server.world.schematic.Schematic;
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
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;

@Getter
public class MinecraftServerImpl implements MinecraftServer {

    private static final int NETWORK_THREADS = Runtime.getRuntime().availableProcessors();

    private static final ExecutorService asyncExecutor = Executors.newWorkStealingPool();
    private static final ScheduledExecutorService tickExecutor = Executors
            .newSingleThreadScheduledExecutor(TickThreadFactory.INSTANCE);

    private static final File worldFolder = new File("worlds");

    private final Byte2ObjectOpenHashMap<WorldImpl> worlds = new Byte2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<EntityImpl> entities = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<PlayerImpl> players = new Int2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<Connection, PlayerImpl> playerConnections = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, PlayerImpl> playerNames = new Object2ObjectOpenHashMap<>();
    private int nextEntityId, nextWorldId;

    private final Set<Connection> connections = new ObjectOpenHashSet<>();

    private final TickLoopImpl tickLoop = new TickLoopImpl(this);

    private final CommandMapImpl registeredCommands = new CommandMapImpl() {
        {
            register(new TPSCommand());
            register(new VersionCommand());
            register(new KnockbackCommand());
        }
    };

    private static final World.Generator DEFAULT_GENERATOR = new World.Generator() {
        @Override
        public ChunkImpl generate(World world, byte chunkX, byte chunkZ) {
            val chunk = new ChunkImpl(world, chunkX, chunkZ);

            for (int x = 0; x < 16; x++)
                for (int z = 0; z < 16; z++)
                    chunk.setType(x, z, (short) 50, 1);

            return chunk;
        }
    };

    private EventLoopGroup group;
    private boolean running = false;

    @Setter
    // TODO: add ability to filter debug messages using groovy config
    public boolean debugMessages = false;

    public PlayerImpl createPlayer(ConnectionImpl connection) throws IllegalStateException {
        val spawnWorld = worlds.get((byte) 0);

        if (spawnWorld == null)
            throw new IllegalStateException("Spawn world not found for player.");

        val player = new PlayerImpl(connection, nextEntityId++, spawnWorld);
        addPlayer(connection, player);
        return player;
    }

    public void addPlayer(ConnectionImpl connection, PlayerImpl player) throws IllegalStateException {
        if (players.put(player.getId(), player) != null)
            throw new IllegalStateException("Player with id " + player.getId() + " already exists.");
        if (playerConnections.put(connection, player) != null)
            throw new IllegalStateException("Player with connection " + connection + " already exists.");
        if (playerNames.put(player.getName(), player) != null)
            throw new IllegalStateException("Player with name " + player.getName() + " already exists.");
        addEntity(player);
    }

    public void addEntity(EntityImpl entity) throws IllegalStateException {
        if (entities.put(entity.getId(), entity) != null)
            throw new IllegalStateException("Entity with id " + entity.getId() + " already exists.");
    }

    public void removeEntity(int id) {
        entities.remove(id);

        val player = players.remove(id);

        if (player != null)
            player.disconnect(Messages.DISCONNECT_UNKNOWN);
    }

    public void disconnected(ConnectionImpl connection) {
        val player = playerConnections.remove(connection);
        if (player != null) {
            players.remove(player.getId());
            playerNames.remove(player.getName());
            entities.remove(player.getId());
        }
    }

    public WorldImpl createWorld(String name, Environment environment)
            throws IllegalStateException {
        return createWorld(name, environment, DEFAULT_GENERATOR);
    }

    public WorldImpl createWorld(String name, Environment environment, Generator generator)
            throws IllegalStateException {
        byte id = (byte) nextWorldId++;
        val world = new WorldImpl(id, name, environment, generator, this);
        if (worlds.put(id, world) != null)
            throw new IllegalStateException("World with id " + id + " already exists.");
        return world;
    }

    @SneakyThrows
    @Override
    public void start(int port) {
        if (running)
            throw new IllegalStateException("Server is already running.");

        running = true;
        if (!worldFolder.exists())
            worldFolder.mkdirs();

        for (val file : worldFolder.listFiles()) {
            if (file.getName().endsWith(".schematic")) {
                val name = file.getName().substring(0, file.getName().length() - 10);
                val schematic = Schematic.load(file);
                System.out.println("[Mineral] Loaded schematic " + name + " with " + schematic.getChunkedBlocks().size()
                        + " chunks.");

                createWorld(name, Environment.NORMAL, new Generator() {
                    @Override
                    public ChunkImpl generate(World world, byte chunkX, byte chunkZ) {
                        try {
                            val chunk = new ChunkImpl(world, chunkX, chunkZ);

                            val blocks = schematic.getBlocksForChunk(chunkX, chunkZ);

                            for (val block : blocks) {
                                int x = block.getX(), y = block.getY(), z = block.getZ();
                                int localX = x & 15, localZ = z & 15;
                                chunk.setType(localX, localZ, (short) y, block.getType());
                                chunk.setMetaData(localX, localZ, (short) y, block.getData());
                            }

                            return chunk;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new ChunkImpl(world, chunkX, chunkZ);
                        }
                    }
                });
            }
        }

        if (worlds.isEmpty())
            createWorld("Spawn", World.Environment.NORMAL);

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

    @SneakyThrows
    @Override
    public void stop() {
        tickExecutor.shutdown();
        asyncExecutor.shutdown();
        if (group != null)
            group.shutdownGracefully().sync();
    }

    @Override
    public ScheduledExecutorService getTickExecutor() {
        return tickExecutor;
    }

    @Override
    public ExecutorService getAsyncExecutor() {
        return asyncExecutor;
    }

    public static void main(String[] args) {
        new MinecraftServerImpl().start(25565);
    }

    @Override
    public Collection<Player> getOnlinePlayers() {
        return Collections.unmodifiableCollection(players.values());
    }

    @Override
    public Player getPlayer(String name) {
        return playerNames.get(name);
    }

    @Override
    public Player getPlayer(int entityId) {
        return players.get(entityId);
    }

}
