package gg.mineral.server.snapshot

import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.snapshot.AsyncServerSnapshot
import gg.mineral.server.MinecraftServerImpl
import gg.mineral.server.entity.EntityImpl
import gg.mineral.server.entity.living.human.PlayerImpl
import gg.mineral.server.network.channel.FakeChannelImpl
import gg.mineral.server.network.connection.ConnectionImpl
import gg.mineral.server.tick.TickLoopImpl
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import kotlinx.coroutines.sync.Mutex
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

class AsyncServerSnapshotImpl(serverImpl: MinecraftServerImpl) : AsyncServerSnapshot,
    TickLoopImpl(serverImpl) {
    companion object {
        private val threadCounter = AtomicInteger()
        private val LOGGER: Logger = LogManager.getLogger(
            MinecraftServerImpl::class.java
        )
    }

    override val name = "Mineral-async-" + threadCounter.getAndIncrement()
    val entities = Int2ObjectOpenHashMap<EntityImpl?>()
    val players = Int2ObjectOpenHashMap<PlayerImpl?>()
    val playerNames = Object2ObjectOpenHashMap<String, PlayerImpl?>()
    val playerConnections = Object2ObjectOpenHashMap<ConnectionImpl, PlayerImpl?>()
    val connectionsMutex = Mutex()
    override val connections: MutableSet<Connection> = ObjectOpenHashSet()
    private val syncedTaskQueue = ConcurrentLinkedQueue<Runnable>()
    val millis: Long
        get() = (System.nanoTime() / 1e6).toLong()


    init {
        start()
    }

    override fun tick() {
        super.tick()

        while (!syncedTaskQueue.isEmpty()) syncedTaskQueue.poll().run()
        connections.forEach { it.call() }
        entities.values.forEach { it?.tick() }
    }

    suspend fun disconnected(connection: ConnectionImpl) = playerConnections.remove(connection)?.let {
        LOGGER.info("{} has disconnected. [UUID: {}].", it.name, it.uuid)
        players.remove(it.id)
        playerNames.remove(it.name)
        entities.remove(it.id)?.cleanup()
    }

    @kotlin.Throws(IllegalStateException::class)
    fun createPlayer(connection: ConnectionImpl): PlayerImpl {
        val spawnWorld = server.worlds[0.toByte()]

        checkNotNull(spawnWorld) { "Spawn world not found for player." }

        val player = PlayerImpl(connection, server.nextEntityId.getAndIncrement(), spawnWorld)
        if (!player.onJoin()) return player
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
            connection.channel is FakeChannelImpl || playerNames.put(
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

    suspend fun removeEntity(id: Int) {
        entities.remove(id)?.cleanup()
        players.remove(id)?.disconnect(server.config.disconnectUnknown)
    }

    override suspend fun getPlayer(name: String) = playerNames[name]

    override suspend fun getPlayer(entityId: Int) = players[entityId]

    override fun execute(runnable: Runnable) {
        syncedTaskQueue.add(runnable)
    }

    override suspend fun getOnlinePlayers(): Collection<Player> {
        return Collections.unmodifiableCollection<Player>(players.values)
    }

    override suspend fun getOnlineCount(): Int {
        return players.size
    }
}