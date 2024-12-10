package gg.mineral.server

import gg.mineral.api.MinecraftServer
import gg.mineral.api.command.CommandExecutor
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.plugin.MineralPlugin
import gg.mineral.api.plugin.event.Event
import gg.mineral.api.world.World
import gg.mineral.server.command.CommandMapImpl
import gg.mineral.server.command.impl.KnockbackCommand
import gg.mineral.server.command.impl.StopCommand
import gg.mineral.server.command.impl.TPSCommand
import gg.mineral.server.command.impl.VersionCommand
import gg.mineral.server.config.GroovyConfig
import gg.mineral.server.entity.EntityImpl
import gg.mineral.server.entity.living.human.PlayerImpl
import gg.mineral.server.network.channel.MineralChannelInitializer
import gg.mineral.server.network.connection.ConnectionImpl
import gg.mineral.server.tick.TickLoopImpl
import gg.mineral.server.tick.TickThreadFactory
import gg.mineral.server.world.WorldImpl
import gg.mineral.server.world.chunk.ChunkImpl
import gg.mineral.server.world.schematic.Schematic
import groovy.lang.Binding
import groovy.lang.GroovyShell
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.EventLoopGroup
import io.netty.channel.epoll.Epoll
import io.netty.channel.epoll.EpollEventLoopGroup
import io.netty.channel.epoll.EpollServerSocketChannel
import io.netty.channel.kqueue.KQueue
import io.netty.channel.kqueue.KQueueEventLoopGroup
import io.netty.channel.kqueue.KQueueServerSocketChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import lombok.Cleanup
import lombok.Getter
import lombok.SneakyThrows
import net.minecrell.terminalconsole.SimpleTerminalConsole
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.codehaus.groovy.control.CompilationFailedException
import org.jline.reader.*
import java.io.File
import java.io.InputStreamReader
import java.net.InetSocketAddress
import java.net.URLClassLoader
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

@Getter
class MinecraftServerImpl : SimpleTerminalConsole(), MinecraftServer, CommandExecutor,
    Completer {
    private val worlds = Byte2ObjectOpenHashMap<WorldImpl?>()
    private val entities = Int2ObjectOpenHashMap<EntityImpl?>()
    private val players = Int2ObjectOpenHashMap<PlayerImpl>()
    private val playerConnections = Object2ObjectOpenHashMap<Connection, PlayerImpl?>()
    private val playerNames = Object2ObjectOpenHashMap<String, PlayerImpl?>()
    private val connections: Set<Connection> = ObjectOpenHashSet()

    private val tickLoop = TickLoopImpl(this)

    private val config = GroovyConfig()

    private val loadedPlugins: Set<MineralPlugin> = ObjectOpenHashSet()

    private val registeredCommands: CommandMapImpl = object : CommandMapImpl() {
        init {
            register(TPSCommand())
            register(VersionCommand())
            register(KnockbackCommand())
            register(StopCommand())
        }
    }
    private var nextEntityId = 0
    private var nextWorldId = 0
    private var group: EventLoopGroup? = null
    private var running = false

    @kotlin.Throws(IllegalStateException::class)
    fun createPlayer(connection: ConnectionImpl): PlayerImpl {
        val spawnWorld = worlds[0.toByte()]

        checkNotNull(spawnWorld) { "Spawn world not found for player." }

        val player = PlayerImpl(connection, nextEntityId++, spawnWorld)
        addPlayer(connection, player)
        return player
    }

    @kotlin.Throws(IllegalStateException::class)
    fun addPlayer(connection: ConnectionImpl, player: PlayerImpl) {
        check(players.put(player.id, player) == null) { "Player with id " + player.id + " already exists." }
        check(
            playerConnections.put(
                connection,
                player
            ) == null
        ) { "Player with connection $connection already exists." }
        check(playerNames.put(player.name, player) == null) { "Player with name " + player.name + " already exists." }
        addEntity(player)
    }

    @kotlin.Throws(IllegalStateException::class)
    fun addEntity(entity: EntityImpl) {
        check(entities.put(entity.id, entity) == null) { "Entity with id " + entity.id + " already exists." }
    }

    fun removeEntity(id: Int) {
        entities.remove(id)

        val player = players.remove(id)

        player?.disconnect(config.disconnectUnknown)
    }

    fun disconnected(connection: ConnectionImpl) {
        val player = playerConnections.remove(connection)
        if (player != null) {
            LOGGER.info("{} has disconnected. [UUID: {}].", player.name, player.uuid)
            players.remove(player.id)
            playerNames.remove(player.name)
            entities.remove(player.id)
        }
    }

    @kotlin.Throws(IllegalStateException::class)
    fun createWorld(name: String?, environment: World.Environment?): WorldImpl {
        return createWorld(name, environment, DEFAULT_GENERATOR)
    }

    @kotlin.Throws(IllegalStateException::class)
    fun createWorld(name: String?, environment: World.Environment?, generator: World.Generator?): WorldImpl {
        val id = (nextWorldId++).toByte()
        val world = WorldImpl(id, name, environment, generator, this)
        check(worlds.put(id, world) == null) { "World with id $id already exists." }
        return world
    }

    @SneakyThrows
    override fun start() {
        check(!running) { "Server is already running." }

        running = true
        val startTime = System.nanoTime()
        LOGGER.info("Starting server...")

        config.load()

        LOGGER.info(
            "Config at {} has been loaded successfully [{}ms].",
            config.configFile.absolutePath,
            (System.nanoTime() - startTime) / 1000000
        )

        this.loadPlugins(File(config.pluginsFolder))

        LOGGER.info(
            "Loaded {} plugins [{}ms].",
            this.getLoadedPlugins().size,
            (System.nanoTime() - startTime) / 1000000
        )

        val port = config.port
        val worldFolder = File(config.worldsFolder)

        worldFolder.mkdirs()

        val files = worldFolder.listFiles()

        if (files != null) for (file in files) {
            if (file.name.endsWith(".schematic")) {
                val name = file.name.substring(0, file.name.length - 10)
                val schematic = Schematic.load(file)
                LOGGER.info(
                    "Loaded schematic {} with {} chunks [{}ms].",
                    name,
                    schematic.chunkedBlocks.size(),
                    (System.nanoTime() - startTime) / 1000000
                )

                createWorld(
                    name, World.Environment.NORMAL
                ) { world: World?, chunkX: Byte, chunkZ: Byte ->
                    try {
                        val chunk = ChunkImpl(world, chunkX, chunkZ)

                        val blocks = schematic.getBlocksForChunk(chunkX, chunkZ)

                        for (block in blocks) {
                            val x = block.x
                            val y = block.y
                            val z = block.z
                            val localX = x and 15
                            val localZ = z and 15
                            chunk.setType(localX, localZ, y.toShort(), block.type)
                            chunk.setMetaData(localX, localZ, y.toShort().toInt(), block.data)
                        }

                        return@createWorld chunk
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return@createWorld ChunkImpl(world, chunkX, chunkZ)
                    }
                }
            }
        }

        if (worlds.isEmpty()) createWorld("Spawn", World.Environment.NORMAL)

        group = if (Epoll.isAvailable()) EpollEventLoopGroup(NETWORK_THREADS)
        else if (KQueue.isAvailable()) KQueueEventLoopGroup(NETWORK_THREADS)
        else NioEventLoopGroup(NETWORK_THREADS)

        val bootstrap = ServerBootstrap()
        bootstrap.group(group)
            .channel(
                if (Epoll.isAvailable())
                    EpollServerSocketChannel::class.java
                else
                    if (KQueue.isAvailable())
                        KQueueServerSocketChannel::class.java
                    else
                        NioServerSocketChannel::class.java
            )
            .localAddress(InetSocketAddress(port))
            .childHandler(MineralChannelInitializer(this)).bind().sync()

        tickLoop.start()

        LOGGER.info("Server started on port {} [{}ms].", port, (System.nanoTime() - startTime) / 1000000)

        super.start()
    }

    override fun getTickExecutor(): ScheduledExecutorService {
        return Companion.tickExecutor
    }

    override fun getAsyncExecutor(): ExecutorService {
        return Companion.asyncExecutor
    }

    override fun getOnlinePlayers(): Collection<Player> {
        return Collections.unmodifiableCollection<Player>(players.values)
    }

    override fun getPlayer(name: String): Player {
        return playerNames[name]!!
    }

    override fun getPlayer(entityId: Int): Player {
        return players[entityId]
    }

    override fun getPermissions(): Set<String> {
        return Companion.permissions
    }

    override fun msg(message: String) {
        LOGGER.info(message)
    }

    override fun getServer(): MinecraftServer {
        return this
    }

    override fun runCommand(input: String) {
        val splitCommand = input.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val args = if (splitCommand.size > 1)
            Arrays.copyOfRange(splitCommand, 1, splitCommand.size)
        else
            arrayOfNulls(0)

        val commandName = splitCommand[0]
        val command = registeredCommands[input]

        if (command != null) command.execute(this, args)
        else LOGGER.error("Unknown command: {}", commandName)
    }

    @SneakyThrows
    override fun shutdown() {
        this.running = false
        Companion.tickExecutor.shutdown()
        Companion.asyncExecutor.shutdown()
        if (group != null) group!!.shutdownGracefully().sync()
    }

    override fun buildReader(builder: LineReaderBuilder): LineReader {
        return super.buildReader(
            builder
                .appName("MineralEngine")
                .completer(this)
        )
    }

    override fun complete(reader: LineReader, line: ParsedLine, candidates: MutableList<Candidate>) {
        val buffer = line.line()

        // line.word() for current word, add autocomplete for arguments
        for (cmd in registeredCommands.keys) if (cmd.startsWith(buffer)) candidates.add(Candidate(cmd))
    }

    private fun loadPlugins(pluginDirectory: File) {
        val files = pluginDirectory.listFiles { dir: File?, name: String -> name.endsWith(".jar") }
        if (files == null) return

        for (file in files) {
            try {
                loadPlugin(file)
            } catch (e: Exception) {
                LOGGER.error("Failed to load plugin from {}", file.name, e)
            }
        }
    }

    @kotlin.Throws(Exception::class)
    private fun loadPlugin(jarFile: File) {
        @Cleanup val loader = URLClassLoader(arrayOf(jarFile.toURI().toURL()), javaClass.classLoader)

        val pluginClass = loadMainClassFromGroovy(loader, jarFile)
        if (pluginClass == null) {
            LOGGER.error("Failed to find main class in plugin {}", jarFile.name)
            return
        }

        val constructor = pluginClass.getConstructor()
        val plugin = constructor.newInstance()

        this.getLoadedPlugins().add(plugin)
        plugin.onEnable()
        registeredCommands.registerAll(plugin.commands.values)
    }

    @kotlin.Throws(CompilationFailedException::class)
    private fun loadMainClassFromGroovy(loader: URLClassLoader, jarFile: File): Class<out MineralPlugin>? {
        val resource = loader.findResource("plugin.groovy")
        if (resource == null) {
            LOGGER.error("plugin.groovy not found in {}", jarFile.name)
            return null
        }

        val binding = Binding()
        val shell = GroovyShell(loader, binding)

        try {
            resource.openStream().use { inputStream ->
                InputStreamReader(inputStream).use { reader ->
                    shell.evaluate(reader)
                    val mainClass = binding.getVariable("mainClass")
                    if (mainClass is Class<*> && MineralPlugin::class.java.isAssignableFrom(mainClass)) {
                        return mainClass.asSubclass(MineralPlugin::class.java)
                    } else {
                        LOGGER.error("mainClass in plugin.groovy is not a valid MineralPlugin class")
                        return null
                    }
                }
            }
        } catch (e: Exception) {
            LOGGER.error("Failed to parse plugin.groovy in {}", jarFile.name, e)
            return null
        }
    }

    fun disablePlugins() {
        for (plugin in getLoadedPlugins()) {
            try {
                plugin.onDisable()
            } catch (e: Exception) {
                LOGGER.error("Error disabling plugin {}", plugin.javaClass.name, e)
            }
        }
    }

    fun callEvent(event: Event?): Boolean {
        for (plugin in getLoadedPlugins()) for (listener in plugin.listeners) if (listener.onEvent(event)) return true

        return false
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger(
            MinecraftServerImpl::class.java
        )

        private val permissions: Set<String> = ObjectOpenHashSet(listOf("*"))

        private val NETWORK_THREADS = Runtime.getRuntime().availableProcessors()

        private val asyncExecutor: ExecutorService = Executors.newWorkStealingPool()
        private val tickExecutor: ScheduledExecutorService = Executors
            .newSingleThreadScheduledExecutor(TickThreadFactory.INSTANCE)
        private val DEFAULT_GENERATOR = World.Generator { world: World?, chunkX: Byte, chunkZ: Byte ->
            val chunk = ChunkImpl(world, chunkX, chunkZ)
            for (x in 0..15) for (z in 0..15) chunk.setType(x, z, 50.toShort(), 1)
            chunk
        }

        @JvmStatic
        fun main(args: Array<String>) {
            MinecraftServerImpl().start()
        }
    }
}
