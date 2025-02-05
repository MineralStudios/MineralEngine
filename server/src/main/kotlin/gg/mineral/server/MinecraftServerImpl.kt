package gg.mineral.server

import gg.mineral.api.MinecraftServer
import gg.mineral.api.command.CommandExecutor
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.channel.FakeChannel
import gg.mineral.api.network.channel.MineralChannelInitializer
import gg.mineral.api.plugin.MineralPlugin
import gg.mineral.api.plugin.event.Event
import gg.mineral.api.snapshot.ServerSnapshot
import gg.mineral.api.world.World
import gg.mineral.server.command.CommandMapImpl
import gg.mineral.server.command.impl.*
import gg.mineral.server.config.GroovyConfig
import gg.mineral.server.network.channel.FakeChannelImpl
import gg.mineral.server.network.channel.ServerChannelInitializer
import gg.mineral.server.network.connection.ConnectionImpl
import gg.mineral.server.snapshot.AsyncServerSnapshotImpl
import gg.mineral.server.tick.TickThreadFactory
import gg.mineral.server.world.WorldImpl
import gg.mineral.server.world.chunk.ChunkImpl
import gg.mineral.server.world.schematic.Schematic
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
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.sync.withLock
import net.md_5.bungee.api.chat.BaseComponent
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
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.atomic.AtomicInteger

class MinecraftServerImpl(
    override val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(
        cores,
        TickThreadFactory.INSTANCE
    ),
    val syncScope: CoroutineScope = CoroutineScope(Executors.newSingleThreadExecutor().asCoroutineDispatcher()),
    override val permissions: MutableSet<String> = ObjectOpenHashSet(listOf("*")),
    override val name: String = "Mineral-main"
) : SimpleTerminalConsole(), MinecraftServer,
    CommandExecutor,
    Completer {
    val worlds: Byte2ObjectOpenHashMap<WorldImpl> by lazy {
        object : Byte2ObjectOpenHashMap<WorldImpl>() {
            init {
                val startTime = System.nanoTime()
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
                            schematic.chunkedBlocks.size,
                            (System.nanoTime() - startTime) / 1000000
                        )

                        this.createWorld(
                            name, World.Environment.NORMAL
                        ) { world: World, chunkX: Byte, chunkZ: Byte ->
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
                                    chunk.setMetaData(localX, localZ, y.toShort(), block.data)
                                }

                                return@createWorld chunk
                            } catch (e: Exception) {
                                e.printStackTrace()
                                return@createWorld ChunkImpl(world, chunkX, chunkZ)
                            }
                        }
                    }
                }

                if (this.isEmpty()) this.createWorld("Spawn", World.Environment.NORMAL)
            }

            @kotlin.Throws(IllegalStateException::class)
            fun createWorld(
                name: String,
                environment: World.Environment,
                generator: World.Generator = DEFAULT_GENERATOR
            ): WorldImpl {
                val id = (nextWorldId++).toByte()
                val world = WorldImpl(id, name, environment, generator, this@MinecraftServerImpl)
                check(this.put(id, world) == null) { "World with id $id already exists." }
                return world
            }

            override fun get(key: Byte): WorldImpl? = super.get(key)

            override fun getOrDefault(key: Byte, defaultValue: WorldImpl): WorldImpl? =
                super.getOrDefault(key, defaultValue)

            override fun containsKey(key: Byte): Boolean = super.containsKey(key)

            override fun remove(k: Byte): WorldImpl? = super.remove(k)
        }
    }

    override val server: MinecraftServer get() = this
    override val serverSnapshot: ServerSnapshot
        get() = this
    override val snapshots: Array<ServerSnapshot> by lazy {
        Array(cores) { AsyncServerSnapshotImpl(this) }
    }
    private val currentSnapshotIndex = AtomicInteger(0)

    private val currentSnapshot: ServerSnapshot
        get() = snapshots[currentSnapshotIndex.getAndIncrement() % snapshots.size]

    val config = GroovyConfig()

    private val loadedPlugins: ObjectOpenHashSet<MineralPlugin> by lazy {
        object : ObjectOpenHashSet<MineralPlugin>() {
            init {
                val startTime = System.nanoTime()
                this.loadPlugins(File(config.pluginsFolder))

                LOGGER.info(
                    "Loaded {} plugins [{}ms].",
                    this.size,
                    (System.nanoTime() - startTime) / 1000000
                )
            }

            private fun loadPlugins(pluginDirectory: File) {
                val files = pluginDirectory.listFiles { _: File?, name -> name.endsWith(".jar") }
                if (files == null) return

                for (file in files) {
                    try {
                        loadPlugin(file)
                    } catch (e: Exception) {
                        LOGGER.error("Failed to load plugin from {}", file.name, e)
                    }
                }
            }

            @Throws(Exception::class)
            private fun loadPlugin(jarFile: File) {
                val loader = URLClassLoader(arrayOf(jarFile.toURI().toURL()), javaClass.classLoader)

                val pluginClass = loadMainClassFromGroovy(loader, jarFile)
                if (pluginClass == null) {
                    LOGGER.error("Failed to find main class in plugin {}", jarFile.name)
                    return
                }

                val constructor = pluginClass.getConstructor()
                val plugin = constructor.newInstance()
                plugin.server = this@MinecraftServerImpl

                this.add(plugin)
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

                val binding = groovy.lang.Binding()
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
        }
    }

    override val registeredCommands: CommandMapImpl by lazy {
        object : CommandMapImpl() {
            init {
                register(TPSCommand())
                register(PingCommand())
                register(VersionCommand())
                register(KnockbackCommand())
                register(StopCommand())
            }
        }
    }
    val nextEntityId = AtomicInteger(0)
    private var nextWorldId = 0
    private var group: EventLoopGroup? = null
    private var running = false
    private val channelInitializer = ServerChannelInitializer(this)

    override fun start(networkThreads: Int) {
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

        group = if (Epoll.isAvailable()) EpollEventLoopGroup(networkThreads)
        else if (KQueue.isAvailable()) KQueueEventLoopGroup(networkThreads)
        else NioEventLoopGroup(networkThreads)

        val bootstrap = ServerBootstrap()
        bootstrap.group(group)
            .channel(
                if (Epoll.isAvailable())
                    EpollServerSocketChannel::class.java
                else if (KQueue.isAvailable())
                    KQueueServerSocketChannel::class.java
                else
                    NioServerSocketChannel::class.java
            )
            .localAddress(InetSocketAddress(config.port))
            .childHandler(channelInitializer).bind().sync()

        LOGGER.info("Server started on port {} [{}ms].", config.port, (System.nanoTime() - startTime) / 1000000)

        LOGGER.info(loadedPlugins.size.toString() + " plugins loaded.")

        super.start()
    }

    override suspend fun broadcastMessage(message: String) {
        for (world in worlds.values) world.broadcastMessage(message)
    }

    override fun msg(message: String) = LOGGER.info(message)
    override fun msg(vararg baseComponents: BaseComponent) {
        msg(BaseComponent.toLegacyText(*baseComponents))
    }

    override fun isRunning(): Boolean = running

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

    override fun shutdown() {
        this.running = false
        if (group != null) group!!.shutdownGracefully().sync()
    }

    override fun createFakeChannel(initializer: MineralChannelInitializer, peerChannel: FakeChannel?) =
        FakeChannelImpl(initializer, if (peerChannel is FakeChannelImpl) peerChannel else null)

    override fun createFakeServerChannel(peerChannel: FakeChannel?) = createFakeChannel(channelInitializer, peerChannel)

    override suspend fun getOnlinePlayers() = worlds.map { it.value.getEntities().filterIsInstance<Player>() }.flatten()

    override suspend fun getOnlineCount(): Int {
        var count = 0
        for (world in worlds.values) count += world.entityCount(Player::class)
        return count
    }

    override fun buildReader(builder: LineReaderBuilder): LineReader = super.buildReader(
        builder
            .appName("MineralEngine")
            .completer(this)
    )

    override fun complete(reader: LineReader, line: ParsedLine, candidates: MutableList<Candidate>) {
        val buffer = line.line()

        // line.word() for current word, add autocomplete for arguments
        for (cmd in registeredCommands.keys) if (cmd!!.startsWith(buffer)) candidates.add(Candidate(cmd))
    }

    fun callEvent(event: Event): Boolean {
        for (plugin in loadedPlugins) for (listener in plugin.listeners) if (listener.onEvent(event)) return true

        return false
    }

    suspend fun newConnection(): ConnectionImpl = currentSnapshot.let {
        check(it is AsyncServerSnapshotImpl) { "Current snapshot is not an AsyncServerSnapshotImpl." }
        return ConnectionImpl(it).also { connection -> it.connectionsMutex.withLock { it.connections.add(connection) } }
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger(
            MinecraftServerImpl::class.java
        )

        private val cores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
        private val executor: ScheduledExecutorService = Executors
            .newScheduledThreadPool(cores, TickThreadFactory.INSTANCE)

        private val DEFAULT_GENERATOR by lazy {
            World.Generator { world: World, chunkX: Byte, chunkZ: Byte ->
                val chunk = ChunkImpl(world, chunkX, chunkZ)
                for (x in 0..15) for (z in 0..15) chunk.setType(x, z, 50.toShort(), 1)
                chunk
            }
        }

        @JvmStatic
        fun main(args: Array<String>) =
            MinecraftServerImpl(executor).start(cores)
    }
}
