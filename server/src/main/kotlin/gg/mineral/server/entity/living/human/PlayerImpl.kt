package gg.mineral.server.entity.living.human

import gg.mineral.api.entity.attribute.Attribute
import gg.mineral.api.entity.attribute.AttributeInstance
import gg.mineral.api.entity.living.human.Player
import gg.mineral.api.entity.living.human.property.PlayerAbilities
import gg.mineral.api.inventory.item.ItemStack
import gg.mineral.api.inventory.item.Material
import gg.mineral.api.math.MathUtil.angleToByte
import gg.mineral.api.math.MathUtil.floor
import gg.mineral.api.math.MathUtil.toFixedPointInt
import gg.mineral.api.plugin.event.player.PlayerJoinEvent
import gg.mineral.api.world.World
import gg.mineral.api.world.chunk.Chunk
import gg.mineral.api.world.property.Difficulty
import gg.mineral.api.world.property.Dimension
import gg.mineral.api.world.property.LevelType
import gg.mineral.server.entity.effect.PotionEffect
import gg.mineral.server.entity.living.HumanImpl
import gg.mineral.server.entity.meta.EntityMetadataImpl
import gg.mineral.server.network.connection.ConnectionImpl
import gg.mineral.server.network.packet.play.bidirectional.HeldItemChangePacket
import gg.mineral.server.network.packet.play.bidirectional.PlayerAbilitiesPacket
import gg.mineral.server.network.packet.play.clientbound.*
import gg.mineral.server.world.WorldImpl
import gg.mineral.server.world.chunk.ChunkImpl
import gg.mineral.server.world.chunk.ChunkImpl.Companion.toKey
import gg.mineral.server.world.chunk.ChunkImpl.Companion.xFromKey
import gg.mineral.server.world.chunk.ChunkImpl.Companion.zFromKey
import gg.mineral.server.world.chunk.EmptyChunkImpl
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet
import it.unimi.dsi.fastutil.shorts.Short2IntLinkedOpenHashMap
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet
import net.md_5.bungee.api.chat.BaseComponent
import net.md_5.bungee.api.chat.TextComponent
import org.apache.logging.log4j.LogManager
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs

open class PlayerImpl(
    override val connection: ConnectionImpl, id: Int, world: WorldImpl
) :
    HumanImpl(id, world, connection.name.toString()), Player {
    private val chunkUpdateTracker by lazy { Short2IntLinkedOpenHashMap() }
    val visibleChunks by lazy { ShortOpenHashSet() }
    override val permissions: MutableSet<String> by lazy { ObjectOpenHashSet() }
    private val attributeModifiers: MutableMap<String, AttributeInstance> by lazy { ConcurrentHashMap() }

    val uuid: UUID?
        get() = connection.uuid

    fun effect(effect: PotionEffect, amplifier: Byte, duration: Short) {
        effect.applyAttributes(this, amplifier.toInt(), duration.toDouble())
        connection.queuePacket(EntityEffectPacket(id, PotionEffect.SPEED.id, amplifier, duration))
    }

    fun disconnect(chatComponent: BaseComponent) {
        connection.disconnect(chatComponent)
    }

    override fun tick() {
        super.tick()

        if (chunkUpdateNeeded || firstTick) updateChunks()

        tickArm()

        if (!firstTick && currentTick % server.config.relativeMoveFrequency == 0) updateVisibleEntities()

        firstTick = false
    }

    private fun updateVisibleEntities() {

        if (!entityRemoveIds.isEmpty()) {
            val ids = entityRemoveIds.toIntArray()
            entityRemoveIds.clear()
            connection.queuePacket(DestroyEntitiesPacket(ids))
        }

        for (e in visibleEntities.int2ObjectEntrySet()) {
            val entity = world.getEntity(e.intKey)
            if (entity == null) {
                visibleEntities.remove(e.intKey)
                entityRemoveIds.add(e.intKey)
                continue
            }

            val x = toFixedPointInt(entity.x)
            val y = toFixedPointInt(entity.y)
            val z = toFixedPointInt(entity.z)
            val yaw = angleToByte(entity.yaw)
            val pitch = angleToByte(entity.pitch)
            val prevLoc = e.setValue(intArrayOf(x, y, z, yaw.toInt(), pitch.toInt()))

            checkNotNull(prevLoc) { "Entity with id " + e.intKey + " has no previous location" }
            val dx = x - prevLoc[0]
            val dy = y - prevLoc[1]
            val dz = z - prevLoc[2]

            val largeMovement =
                dx < Byte.MIN_VALUE || dx > Byte.MAX_VALUE || dy < Byte.MIN_VALUE || dy > Byte.MAX_VALUE || dz < Byte.MIN_VALUE || dz > Byte.MAX_VALUE

            if (largeMovement) {
                connection.queuePacket(
                    EntityTeleportPacket(entity.id, x, y, z, yaw, pitch)
                )
                continue
            }

            val deltaX = dx.toByte()
            val deltaY = dy.toByte()
            val deltaZ = dz.toByte()
            val deltaYaw = (yaw - prevLoc[3]).toByte()
            val deltaPitch = (pitch - prevLoc[4]).toByte()

            val moved = deltaX.toInt() != 0 || deltaY.toInt() != 0 || deltaZ.toInt() != 0
            val rotated = abs(deltaYaw.toDouble()) >= 1 || abs(deltaPitch.toDouble()) >= 1
            if (moved && rotated) connection.queuePacket(
                EntityLookAndRelativeMovePacket(
                    entity.id,
                    deltaX,
                    deltaY, deltaZ,
                    yaw, pitch
                ),
                EntityHeadLookPacket(entity.id, yaw)
            )
            else if (moved) connection.queuePacket(
                EntityRelativeMovePacket(
                    entity.id,
                    deltaX,
                    deltaY, deltaZ
                )
            )
            else if (rotated) connection.queuePacket(
                EntityLookPacket(
                    entity.id,
                    yaw, pitch
                ),
                EntityHeadLookPacket(entity.id, yaw)
            )
        }


        val fastKeyIterator = visibleChunks.iterator()
        while (fastKeyIterator.hasNext()) {
            val key = fastKeyIterator.nextShort()
            val chunk = world.getChunk(key)
            if (chunk is ChunkImpl) {
                val iterator = chunk.entities.iterator()

                while (iterator.hasNext()) {
                    val entityId = iterator.nextInt()

                    if (entityId == id || visibleEntities.containsKey(entityId)) continue

                    val entity = world.getEntity(entityId) as? PlayerImpl

                    entity?.let { player ->
                        val x = toFixedPointInt(player.x)
                        val y = toFixedPointInt(player.y)
                        val z = toFixedPointInt(player.z)
                        val yaw = angleToByte(player.yaw)
                        val pitch = angleToByte(player.pitch)
                        connection.sendPacket(
                            SpawnPlayerPacket(
                                player.id,
                                x, y, z, yaw,
                                pitch, player.uuid.toString(),
                                player.name, /* TODO: player property */ArrayList(), 0.toShort(),  /*
                     * TODO:
                     * held
                     * item
                     */
                                EntityMetadataImpl(PlayerImpl::class.java).entryList /* TODO: entity metadata */
                            )
                        )

                        visibleEntities.put(player.id, intArrayOf(x, y, z, yaw.toInt(), pitch.toInt()))
                    }
                }
            }
        }
    }

    fun onJoin(
        x: Double = this.x,
        y: Double = this.y,
        z: Double = this.z,
        yaw: Float = this.yaw,
        pitch: Float = this.pitch
    ): Boolean {
        val playerJoinEvent = PlayerJoinEvent(this, x, y, z, yaw, pitch)

        if (server.callEvent(playerJoinEvent)) {
            disconnect(server.config.disconnectUnknown)
            return false
        }

        this.x = playerJoinEvent.x
        this.y = playerJoinEvent.y
        this.headY = playerJoinEvent.y + this.height
        this.z = playerJoinEvent.z
        this.yaw = playerJoinEvent.yaw
        this.pitch = playerJoinEvent.pitch
        connection.queuePacket(
            JoinGamePacket(
                this.id, gamemode, Dimension.OVERWORLD, Difficulty.PEACEFUL,
                1000.toShort(), LevelType.FLAT
            ),
            SpawnPositionPacket(floor(x), floor(headY), floor(z)),
            PlayerAbilitiesPacket(
                PlayerAbilities(false, false, true, false, true, 0.5f, 0.1f)
            ),
            HeldItemChangePacket(0.toShort()),
            PlayerPositionAndLookPacket(x, headY, z, yaw, pitch, onGround),
            SetSlotPacket(
                0.toByte(), 36.toShort(),
                ItemStack(Material.DIAMOND_SWORD, 1.toUByte(), 1.toShort())
            )
        )

        LOGGER.info(
            (this.name + " has connected. [UUID: " + this.uuid + "] [IP: " + connection.ipAddress
                    + "]")
        )

        setupAttributes()
        effect(PotionEffect.SPEED, 1.toByte(), 32767.toShort())

        return true
    }

    fun getAttribute(attribute: Attribute): AttributeInstance {
        return attributeModifiers.computeIfAbsent(
            attribute.key
        ) {
            AttributeInstance(
                attribute
            ) { attributeInstance: AttributeInstance ->
                this.onAttributeChanged(
                    attributeInstance
                )
            }
        }
    }

    private fun onAttributeChanged(attributeInstance: AttributeInstance) {
        if (attributeInstance.attribute.isShared) {
            val self = true // TODO: is the self player or other players

            if (self) {
                connection.queuePacket(propertiesPacket)
                // sendPacketToViewersAndSelf(getPropertiesPacket());
            } else {
                // sendPacketToViewers(getPropertiesPacket());
            }
        }
    }

    private fun setupAttributes() {
        for (attribute in BASE_ATTRIBUTES) {
            val attributeInstance = AttributeInstance(
                attribute
            ) { attributeInstance: AttributeInstance ->
                this.onAttributeChanged(
                    attributeInstance
                )
            }
            attributeModifiers[attribute.key] = attributeInstance
        }
    }

    override fun teleport(world: World, x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        super.teleport(world, x, y, z, yaw, pitch)
        connection.queuePacket(PlayerPositionAndLookPacket(x, y, z, yaw, pitch, onGround))
    }

    private val propertiesPacket: EntityPropertiesPacket
        // TODO: send properties packet to viewers
        get() {
            val properties =
                Object2ObjectOpenHashMap<String, Attribute.Property>()
            for (instance in attributeModifiers.values) {
                val property =
                    Attribute.Property(
                        instance.baseValue.toDouble(),
                        instance.modifiers.values
                    )
                properties[instance.attribute.key] = property
            }
            return EntityPropertiesPacket(id, properties)
        }

    fun getAttributeValue(attribute: Attribute) = attributeModifiers[attribute.key]?.value ?: attribute.defaultValue

    override fun equals(other: Any?): Boolean {
        if (other is PlayerImpl) return other.connection == connection

        return false
    }

    override fun hashCode() = connection.hashCode()

    override fun msg(message: String) =
        connection.queuePacket(ChatMessagePacket(arrayOf(TextComponent(message))))

    override fun msg(vararg baseComponents: BaseComponent) {
        connection.queuePacket(ChatMessagePacket(baseComponents))
    }

    private fun updateChunks() {
        this.chunkUpdateNeeded = false

        val iterator = this.chunkUpdateTracker.short2IntEntrySet().fastIterator()

        val currentTick = this.currentTick


        val chunks = getChunkLoadUpdates()

        while (iterator.hasNext()) {
            val entry = iterator.next()
            if (currentTick - entry.intValue > 100) { // linked hashmap to order by eldest entry
                val key = entry.shortKey
                if (this@PlayerImpl.visibleChunks.remove(key)) {
                    chunks.add(EmptyChunkImpl(world, xFromKey(key), zFromKey(key)))

                    val chunk = world.getChunk(key)
                    if (chunk is ChunkImpl) {
                        val entityIterator = chunk.entities.iterator()
                        while (entityIterator.hasNext()) {
                            val id = entityIterator.nextInt()
                            if (id == this.id) continue
                            this@PlayerImpl.visibleEntities.remove(
                                id
                            )
                            this@PlayerImpl.entityRemoveIds.add(id)
                        }
                    }

                    iterator.remove()
                }
                continue
            }

            break
        }

        if (chunks.isEmpty()) return

        if (chunks.size == 1) {
            val chunk = chunks.first()
            if (chunk is ChunkImpl) this.connection.queuePacket(chunk.toPacket(true))
        } else this.connection
            .queuePacket(MapChunkBulkPacket(world.environment == World.Environment.NORMAL, chunks))
    }

    private fun getChunkLoadUpdates(): MutableList<Chunk> {
        val viewDistance = this.viewDistance
        val chunkX = floor(this.x / 16).toByte()
        val chunkZ = floor(this.z / 16).toByte()
        val chunks = ArrayList<Chunk>()

        for (xOffset in -viewDistance..viewDistance) {
            val cX = (chunkX + xOffset).toByte()
            for (zOffset in -viewDistance..viewDistance) {
                val key = toKey(cX, (chunkZ + zOffset).toByte())
                this.chunkUpdateTracker.put(key, this.currentTick)
                if (!this.visibleChunks.contains(key)) {
                    this.visibleChunks.add(key)
                    val chunk = world.getChunk(key)
                    chunks.add(chunk)
                }
            }
        }
        return chunks
    }

    companion object {
        private val LOGGER = LogManager.getLogger(Player::class.java)
        private val BASE_ATTRIBUTES = arrayOf(
            Attribute.MAX_HEALTH,
            Attribute.KNOCKBACK_RESISTANCE,
            Attribute.MOVEMENT_SPEED,
            Attribute.ATTACK_DAMAGE
        )
    }
}