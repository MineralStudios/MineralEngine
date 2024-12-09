package gg.mineral.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.groovy.control.CompilationFailedException;
import org.eclipse.jdt.annotation.Nullable;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.ParsedLine;

import gg.mineral.api.MinecraftServer;
import gg.mineral.api.command.CommandExecutor;
import gg.mineral.api.entity.living.human.Player;
import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.plugin.MineralPlugin;
import gg.mineral.api.plugin.event.Event;
import gg.mineral.api.world.World;
import gg.mineral.api.world.World.Environment;
import gg.mineral.api.world.World.Generator;
import gg.mineral.server.command.CommandMapImpl;
import gg.mineral.server.command.impl.KnockbackCommand;
import gg.mineral.server.command.impl.StopCommand;
import gg.mineral.server.command.impl.TPSCommand;
import gg.mineral.server.command.impl.VersionCommand;
import gg.mineral.server.config.GroovyConfig;
import gg.mineral.server.entity.EntityImpl;
import gg.mineral.server.entity.living.human.PlayerImpl;
import gg.mineral.server.network.channel.MineralChannelInitializer;
import gg.mineral.server.network.connection.ConnectionImpl;
import gg.mineral.server.tick.TickLoopImpl;
import gg.mineral.server.tick.TickThreadFactory;
import gg.mineral.server.world.WorldImpl;
import gg.mineral.server.world.chunk.ChunkImpl;
import gg.mineral.server.world.schematic.Schematic;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
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
import lombok.Cleanup;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.minecrell.terminalconsole.SimpleTerminalConsole;

@Getter
public class MinecraftServerImpl extends SimpleTerminalConsole implements MinecraftServer, CommandExecutor, Completer {

    private static final Logger LOGGER = LogManager.getLogger(MinecraftServer.class);

    private static Set<String> permissions = new ObjectOpenHashSet<>(Arrays.asList("*"));

    private static final int NETWORK_THREADS = Runtime.getRuntime().availableProcessors();

    private static final ExecutorService asyncExecutor = Executors.newWorkStealingPool();
    private static final ScheduledExecutorService tickExecutor = Executors
            .newSingleThreadScheduledExecutor(TickThreadFactory.INSTANCE);

    private final Byte2ObjectOpenHashMap<WorldImpl> worlds = new Byte2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<EntityImpl> entities = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectOpenHashMap<PlayerImpl> players = new Int2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<Connection, PlayerImpl> playerConnections = new Object2ObjectOpenHashMap<>();
    private final Object2ObjectOpenHashMap<String, PlayerImpl> playerNames = new Object2ObjectOpenHashMap<>();
    private int nextEntityId, nextWorldId;

    private final Set<Connection> connections = new ObjectOpenHashSet<>();

    private final TickLoopImpl tickLoop = new TickLoopImpl(this);

    private final GroovyConfig config = new GroovyConfig();

    private final Set<MineralPlugin> loadedPlugins = new ObjectOpenHashSet<>();

    private final CommandMapImpl registeredCommands = new CommandMapImpl() {
        {
            register(new TPSCommand());
            register(new VersionCommand());
            register(new KnockbackCommand());
            register(new StopCommand());
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
            player.disconnect(config.getDisconnectUnknown());
    }

    public void disconnected(ConnectionImpl connection) {
        val player = playerConnections.remove(connection);
        if (player != null) {
            LOGGER.info(player.getName() + " has disconnected. [UUID: " + player.getUuid() + "].");
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
    public void start() {
        if (running)
            throw new IllegalStateException("Server is already running.");

        running = true;
        long startTime = System.nanoTime();
        LOGGER.info("Starting server...");

        config.load();

        LOGGER.info("Config at " + config.getConfigFile().getAbsolutePath() + " has been loaded successfully ["
                + (System.nanoTime() - startTime) / 1_000_000 + "ms].");

        this.loadPlugins(new File(config.getPluginsFolder()));

        LOGGER.info("Loaded " + this.getLoadedPlugins().size() + " plugins ["
                + (System.nanoTime() - startTime) / 1_000_000 + "ms].");

        int port = config.getPort();
        val worldFolder = new File(config.getWorldsFolder());
        if (!worldFolder.exists())
            worldFolder.mkdirs();

        for (val file : worldFolder.listFiles()) {
            if (file.getName().endsWith(".schematic")) {
                val name = file.getName().substring(0, file.getName().length() - 10);
                val schematic = Schematic.load(file);
                LOGGER.info("Loaded schematic " + name + " with " + schematic.getChunkedBlocks().size()
                        + " chunks ["
                        + (System.nanoTime() - startTime) / 1_000_000 + "ms].");

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

        val bootstrap = new ServerBootstrap();
        bootstrap.group(group)
                .channel(Epoll.isAvailable() ? EpollServerSocketChannel.class
                        : KQueue.isAvailable()
                                ? KQueueServerSocketChannel.class
                                : NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                .childHandler(new MineralChannelInitializer(this)).bind().sync();

        tickLoop.start();

        LOGGER.info("Server started on port " + port + " [" + (System.nanoTime() - startTime) / 1_000_000 + "ms].");

        super.start();
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
        new MinecraftServerImpl().start();
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

    @Override
    public Set<String> getPermissions() {
        return permissions;
    }

    @Override
    public void msg(String message) {
        LOGGER.info(message);
    }

    @Override
    public MinecraftServer getServer() {
        return this;
    }

    @Override
    protected void runCommand(String input) {
        val splitCommand = input.split(" ");
        val args = splitCommand.length > 1 ? Arrays.copyOfRange(splitCommand, 1, splitCommand.length)
                : new String[0];

        val commandName = splitCommand[0];
        val command = registeredCommands.get(input);

        if (command != null)
            command.execute(this, args);
        else
            LOGGER.error("Unknown command: " + commandName);
    }

    @Override
    @SneakyThrows
    public void shutdown() {
        this.running = false;
        tickExecutor.shutdown();
        asyncExecutor.shutdown();
        if (group != null)
            group.shutdownGracefully().sync();
    }

    @Override
    protected LineReader buildReader(LineReaderBuilder builder) {
        return super.buildReader(builder
                .appName("MineralEngine")
                .completer(this));
    }

    @Override
    public void complete(LineReader reader, ParsedLine line, List<Candidate> candidates) {
        val buffer = line.line();

        // line.word() for current word, add autocomplete for arguments
        for (val cmd : registeredCommands.keySet())
            if (cmd.startsWith(buffer))
                candidates.add(new Candidate(cmd));
    }

    private void loadPlugins(File pluginDirectory) throws IOException {
        val files = pluginDirectory.listFiles((dir, name) -> name.endsWith(".jar"));
        if (files == null || files.length == 0)
            return;

        for (val file : files) {
            try {
                loadPlugin(file);
            } catch (Exception e) {
                LOGGER.error("Failed to load plugin from " + file.getName(), e);
            }
        }
    }

    private void loadPlugin(File jarFile) throws Exception {
        @Cleanup
        val loader = new URLClassLoader(new URL[] { jarFile.toURI().toURL() }, this.getClass().getClassLoader());

        val pluginClass = loadMainClassFromGroovy(loader, jarFile);
        if (pluginClass == null) {
            LOGGER.error("Failed to find main class in plugin " + jarFile.getName());
            return;
        }

        val constructor = pluginClass.getConstructor();
        val plugin = constructor.newInstance();

        if (plugin == null) {
            LOGGER.error("Failed to instantiate plugin " + pluginClass.getName());
            return;
        }

        this.getLoadedPlugins().add(plugin);
        plugin.onEnable();
        this.registeredCommands.registerAll(plugin.getCommands().values());
    }

    @Nullable
    private Class<? extends MineralPlugin> loadMainClassFromGroovy(URLClassLoader loader, File jarFile)
            throws CompilationFailedException, IOException {
        val resource = loader.findResource("plugin.groovy");
        if (resource == null) {
            LOGGER.error("plugin.groovy not found in " + jarFile.getName());
            return null;
        }

        val binding = new Binding();
        val shell = new GroovyShell(loader, binding);

        try (val inputStream = resource.openStream();
                val reader = new InputStreamReader(inputStream)) {
            shell.evaluate(reader);
            val mainClass = binding.getVariable("mainClass");
            if (mainClass instanceof Class<?> clazz && MineralPlugin.class.isAssignableFrom(clazz)) {
                return clazz.asSubclass(MineralPlugin.class);
            } else {
                LOGGER.error("mainClass in plugin.groovy is not a valid MineralPlugin class");
                return null;
            }
        } catch (Exception e) {
            LOGGER.error("Failed to parse plugin.groovy in " + jarFile.getName(), e);
            return null;
        }
    }

    public void disablePlugins() {
        for (val plugin : getLoadedPlugins()) {
            try {
                plugin.onDisable();
            } catch (Exception e) {
                LOGGER.error("Error disabling plugin " + plugin.getClass().getName(), e);
            }
        }
    }

    public boolean callEvent(Event event) {
        for (val plugin : getLoadedPlugins())
            for (val listener : plugin.getListeners())
                if (listener.onEvent(event))
                    return true;

        return false;
    }

}
