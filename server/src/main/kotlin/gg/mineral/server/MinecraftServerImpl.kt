package gg.mineral.server

import gg.mineral.api.MinecraftServer
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.plugin.MineralPlugin
import gg.mineral.api.plugin.event.Event
import gg.mineral.api.tick.TickLoop
import gg.mineral.api.world.World
import gg.mineral.server.command.CommandMapImpl
import gg.mineral.server.command.impl.*
import gg.mineral.server.config.GroovyConfig
import gg.mineral.server.entity.EntityImpl
import gg.mineral.server.entity.living.human.PlayerImpl
import gg.mineral.server.network.channel.ServerChannelInitializer
import gg.mineral.server.network.connection.ConnectionImpl
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
import io.netty.channel.local.LocalAddress
import io.netty.channel.local.LocalChannel
import io.netty.channel.local.LocalEventLoopGroup
import io.netty.channel.local.LocalServerChannel
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
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
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger

class MinecraftServerImpl(
    override val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(TickThreadFactory()),
    override val asyncExecutor: ExecutorService = Executors.newWorkStealingPool(),
    override val permissions: MutableSet<String> = ObjectOpenHashSet(listOf("*"))
) : SimpleTerminalConsole(), MinecraftServer,
    Completer {
    val syncDispatcher: CoroutineDispatcher = executor.asCoroutineDispatcher()
    val asyncScope = CoroutineScope(asyncExecutor.asCoroutineDispatcher())
    val entities = Int2ObjectOpenHashMap<EntityImpl?>()
    val players = Int2ObjectOpenHashMap<PlayerImpl?>()
    val playerNames = Object2ObjectOpenHashMap<String, PlayerImpl?>()
    val playerConnections = Object2ObjectOpenHashMap<ConnectionImpl, PlayerImpl?>()
    override val connections: MutableSet<Connection> = ObjectOpenHashSet()
    private val syncedTaskQueue = ConcurrentLinkedQueue<Runnable>()
    val millis: Long
        get() = (System.nanoTime() / 1e6).toLong()

    private var currentTicks = 0
    private var tickSection: Long = 0
    private var curTime: Long = 0

    override var tps1: RollingAverageImpl = RollingAverageImpl(60)
    override var tps5: RollingAverageImpl = RollingAverageImpl(60 * 5)
    override var tps15: RollingAverageImpl = RollingAverageImpl(60 * 15)

    private fun startTickLoop() {
        tickSection = System.nanoTime()

        executor.scheduleAtFixedRate(
            {
                try {
                    tick()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }, 50, 50,
            TimeUnit.MILLISECONDS
        )
    }

    class RollingAverageImpl internal constructor(private val size: Int) : TickLoop.RollingAverage {
        private val samples = DoubleArray(size)
        private val times = LongArray(size)
        override val average: Double
            get() = total / time

        private var time = size * SEC_IN_NANO
        private var total = (TPS * SEC_IN_NANO * size).toDouble()
        private var index = 0

        init {
            for (i in 0..<size) {
                samples[i] = TPS.toDouble()
                times[i] = SEC_IN_NANO
            }
        }

        fun add(x: Double, t: Long) {
            time -= times[index]
            total -= samples[index] * times[index]
            samples[index] = x
            times[index] = t
            time += t
            total += x * t
            if (++index == size) index = 0
        }
    }

    private fun tick() {
        try {
            curTime = System.nanoTime()
            if (++currentTicks % SAMPLE_INTERVAL == 0) {
                val diff = curTime - tickSection
                val currentTps = 1E9 / diff * SAMPLE_INTERVAL
                tps1.add(currentTps, diff)
                tps5.add(currentTps, diff)
                tps15.add(currentTps, diff)
                tickSection = curTime
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        while (!syncedTaskQueue.isEmpty()) syncedTaskQueue.poll().run()
        synchronized(this.connections) {
            connections.forEach { it.call() }
        }
        synchronized(entities) {
            entities.values.forEach { it?.tick() }
        }
    }

    fun disconnected(connection: ConnectionImpl) = playerConnections.remove(connection)?.let {
        LOGGER.info("{} has disconnected. [UUID: {}].", it.name, it.uuid)
        synchronized(players) {
            players.remove(it.id)
            playerNames.remove(it.name)
        }
        synchronized(entities) {
            entities.remove(it.id)?.cleanup()
        }
    }

    @kotlin.Throws(IllegalStateException::class)
    fun createPlayer(
        connection: ConnectionImpl, x: Double = 0.0,
        y: Double = 0.0,
        z: Double = 70.0,
        yaw: Float = 0.0f,
        pitch: Float = 0.0f
    ): PlayerImpl {
        val spawnWorld = worlds[0.toByte()]

        checkNotNull(spawnWorld) { "Spawn world not found for player." }

        val player = PlayerImpl(connection, nextEntityId.getAndIncrement(), spawnWorld)
        if (!player.onJoin(x, y, z, yaw, pitch)) return player
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
        check(
            connection.channel is LocalChannel || playerNames.put(
                player.name,
                player
            ) == null
        ) { "Player with name " + player.name + " already exists." }
        addEntity(player)
    }

    @kotlin.Throws(IllegalStateException::class)
    fun addEntity(entity: EntityImpl) {
        synchronized(entities) {
            check(entities.put(entity.id, entity) == null) { "Entity with id " + entity.id + " already exists." }
        }
    }

    fun removeEntity(id: Int) {
        synchronized(entities) {
            entities.remove(id)?.cleanup()
        }
        synchronized(players) {
            players.remove(id)?.disconnect(config.disconnectUnknown)
        }
    }

    override fun getPlayer(name: String) = playerNames[name]

    override fun getPlayer(entityId: Int) = players[entityId]

    override fun execute(runnable: Runnable) {
        syncedTaskQueue.add(runnable)
    }

    override fun getOnlinePlayers(): Collection<Player> =
        synchronized(players) { Collections.unmodifiableCollection<Player>(players.values) }

    private val worlds: Byte2ObjectOpenHashMap<WorldImpl> by lazy {
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

    val config = GroovyConfig()

    private val loadedPlugins: ObjectOpenHashSet<MineralPlugin> by lazy {
        object : ObjectOpenHashSet<MineralPlugin>() {
            init {
                this.loadPlugins(File(config.pluginsFolder))
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
    private val nextEntityId = AtomicInteger(0)
    private var nextWorldId = 0
    private var group: EventLoopGroup? = null
    private var fakeGroup: EventLoopGroup? = null
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

        val fakeBootstrap = ServerBootstrap()
        val localAddress = LocalAddress("Mineral-fake")
        fakeGroup = LocalEventLoopGroup()
        fakeBootstrap.group(fakeGroup)
            .channel(LocalServerChannel::class.java)
            .localAddress(localAddress)
            .childHandler(channelInitializer).bind().sync()

        LOGGER.info(
            "Server started on port {} [{}ms] [${loadedPlugins.size} plugin(s)].",
            config.port,
            (System.nanoTime() - startTime) / 1000000
        )

        startTickLoop()

        super.start()
    }

    override fun broadcastMessage(message: String) {
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
        if (fakeGroup != null) fakeGroup!!.shutdownGracefully().sync()
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

    fun newConnection() = ConnectionImpl(this).also { connection ->
        synchronized(this.connections) {
            this.connections.add(connection)
        }
    }

    companion object {
        private val LOGGER: Logger = LogManager.getLogger(
            MinecraftServerImpl::class.java
        )

        private const val TPS = 20
        private const val SEC_IN_NANO: Long = 1000000000
        private const val SAMPLE_INTERVAL = 20

        private val cores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
        private val executor: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor(TickThreadFactory())

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
