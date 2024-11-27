package gg.mineral.server.entity.living.human;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.chat.component.StringChatComponent;
import gg.mineral.api.entity.attribute.Attribute;
import gg.mineral.api.entity.attribute.AttributeInstance;
import gg.mineral.api.entity.living.human.Player;
import gg.mineral.api.entity.living.human.property.PlayerAbilities;
import gg.mineral.api.inventory.item.ItemStack;
import gg.mineral.api.inventory.item.Material;
import gg.mineral.api.world.property.Difficulty;
import gg.mineral.api.world.property.Dimension;
import gg.mineral.api.world.property.LevelType;
import gg.mineral.server.MinecraftServerImpl;
import gg.mineral.server.entity.effect.PotionEffect;
import gg.mineral.server.entity.living.HumanImpl;
import gg.mineral.server.entity.meta.EntityMetadataImpl;
import gg.mineral.server.network.connection.ConnectionImpl;
import gg.mineral.server.network.packet.login.clientbound.LoginSuccessPacket;
import gg.mineral.server.network.packet.play.bidirectional.HeldItemChangePacket;
import gg.mineral.server.network.packet.play.bidirectional.PlayerAbilitiesPacket;
import gg.mineral.server.network.packet.play.clientbound.ChatMessagePacket;
import gg.mineral.server.network.packet.play.clientbound.DestroyEntitiesPacket;
import gg.mineral.server.network.packet.play.clientbound.EntityEffectPacket;
import gg.mineral.server.network.packet.play.clientbound.EntityHeadLookPacket;
import gg.mineral.server.network.packet.play.clientbound.EntityLookAndRelativeMovePacket;
import gg.mineral.server.network.packet.play.clientbound.EntityLookPacket;
import gg.mineral.server.network.packet.play.clientbound.EntityPropertiesPacket;
import gg.mineral.server.network.packet.play.clientbound.EntityRelativeMovePacket;
import gg.mineral.server.network.packet.play.clientbound.EntityTeleportPacket;
import gg.mineral.server.network.packet.play.clientbound.JoinGamePacket;
import gg.mineral.server.network.packet.play.clientbound.PlayerPositionAndLookPacket;
import gg.mineral.server.network.packet.play.clientbound.SetSlotPacket;
import gg.mineral.server.network.packet.play.clientbound.SpawnPlayerPacket;
import gg.mineral.server.network.packet.play.clientbound.SpawnPositionPacket;
import gg.mineral.server.world.WorldImpl;
import gg.mineral.server.world.chunk.ChunkImpl;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.Short2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import lombok.Getter;
import lombok.val;

public class PlayerImpl extends HumanImpl implements Player {

    @Getter
    private final Short2IntLinkedOpenHashMap chunkUpdateTracker = new Short2IntLinkedOpenHashMap();
    @Getter
    private final ShortSet visibleChunks = new ShortOpenHashSet();
    @Getter
    private final ConnectionImpl connection;
    @Getter
    private final Set<String> permissions = new ObjectOpenHashSet<>();

    public PlayerImpl(ConnectionImpl connection, int id, WorldImpl world) {
        super(id, world);
        this.connection = connection;
    }

    public String getName() {
        return connection.getName();
    }

    public UUID getUuid() {
        return connection.getUuid();
    }

    public void effect(PotionEffect effect, byte amplifier, short duration) {
        effect.applyAttributes(this, amplifier, duration);
        connection.queuePacket(new EntityEffectPacket(id, PotionEffect.SPEED.getId(), amplifier, duration));
    }

    public void disconnect(BaseChatComponent chatComponent) {
        getConnection().disconnect(chatComponent);
    }

    @Override
    public void setWorld(WorldImpl world) {
        super.setWorld(world);
        if (this.visibleChunks != null)
            this.visibleChunks.clear();
    }

    @Override
    public void tickAsync() {
        if (isChunkUpdateNeeded() || isFirstAsyncTick())
            world.updateChunks(this);

        setFirstAsyncTick(false);
    }

    @Override
    public void tick() {
        super.tick();

        world.updatePosition(this);

        if (!isFirstTick() && getCurrentTick() % 2 == 0)
            updateVisibleEntities();

        tickArm();

        setFirstTick(false);
    }

    public void updateVisibleEntities() {

        for (val entry : visibleEntities.int2ObjectEntrySet())
            if (world.getEntity(entry.getIntKey()) == null)
                visibleEntities.remove(entry.getIntKey());

        if (!entityRemoveIds.isEmpty()) {
            val ids = entityRemoveIds.toIntArray();
            entityRemoveIds.clear();
            getConnection().queuePacket(new DestroyEntitiesPacket(ids));
        }

        for (val e : visibleEntities.int2ObjectEntrySet()) {
            val entity = world.getEntity(e.getIntKey());

            if (entity == null)
                throw new IllegalStateException("Entity with id " + e.getIntKey() + " is not in the world");

            int x = toFixedPointInt(entity.getX()), y = toFixedPointInt(entity.getY()),
                    z = toFixedPointInt(entity.getZ());
            byte yaw = angleToByte(entity.getYaw()),
                    pitch = angleToByte(entity.getPitch());
            val prevLoc = e.setValue(new int[] { x, y, z, yaw, pitch });

            if (prevLoc == null)
                throw new IllegalStateException("Entity with id " + e.getIntKey() + " has no previous location");
            int dx = x - prevLoc[0], dy = y - prevLoc[1], dz = z - prevLoc[2];

            boolean largeMovement = dx < Byte.MIN_VALUE || dx > Byte.MAX_VALUE
                    || dy < Byte.MIN_VALUE || dy > Byte.MAX_VALUE
                    || dz < Byte.MIN_VALUE || dz > Byte.MAX_VALUE;

            if (largeMovement) {
                connection.queuePacket(
                        new EntityTeleportPacket(entity.getId(), x, y, z, yaw, pitch));
                continue;
            }

            byte deltaX = (byte) dx, deltaY = (byte) dy, deltaZ = (byte) dz;
            byte deltaYaw = (byte) (yaw - prevLoc[3]),
                    deltaPitch = (byte) (pitch - prevLoc[4]);

            boolean moved = deltaX != 0 || deltaY != 0 || deltaZ != 0,
                    rotated = Math.abs(deltaYaw) >= 1 || Math.abs(deltaPitch) >= 1;
            if (moved && rotated)
                connection.queuePacket(new EntityLookAndRelativeMovePacket(entity.getId(),
                        deltaX,
                        deltaY, deltaZ,
                        yaw, pitch),
                        new EntityHeadLookPacket(entity.getId(), yaw));
            else if (moved)
                connection.queuePacket(new EntityRelativeMovePacket(entity.getId(),
                        deltaX,
                        deltaY, deltaZ));
            else if (rotated)
                connection.queuePacket(new EntityLookPacket(entity.getId(),
                        yaw, pitch),
                        new EntityHeadLookPacket(entity.getId(), yaw));
        }

        for (short key : visibleChunks) {
            if (world.getChunk(key) instanceof ChunkImpl chunk) {
                val newlyVisible = chunk.getEntities();

                for (int entityId : newlyVisible) {
                    if (entityId == this.getId())
                        continue;

                    val player = world.getPlayer(entityId);

                    if (player == null)
                        continue;

                    if (visibleEntities.containsKey(entityId))
                        continue;

                    final int x = toFixedPointInt(player.getX()),
                            y = toFixedPointInt(player.getY()),
                            z = toFixedPointInt(player.getZ());
                    byte yaw = angleToByte(player.getYaw()),
                            pitch = angleToByte(player.getPitch());
                    connection.sendPacket(new SpawnPlayerPacket(player.getId(), // TODO: Fix not being able to see
                                                                                // player
                            // sometimes
                            x, y, z, yaw,
                            pitch, player.getUuid().toString(),
                            player.getName(), new ArrayList<>() /* TODO: player property */, (short) 0 /*
                                                                                                        * TODO:
                                                                                                        * held
                                                                                                        * item
                                                                                                        */,
                            new EntityMetadataImpl(PlayerImpl.class).getEntryList()/* TODO: entity metadata */));
                    visibleEntities.put(player.getId(), new int[] { x, y, z, yaw, pitch });
                }
            }
        }
    }

    public void onJoin() {
        this.setX(52);
        this.setY(12);
        this.setHeadY(12 + this.height);
        this.setZ(14);
        connection.queuePacket(new LoginSuccessPacket(connection.getUuid(), connection.getName()),
                new JoinGamePacket(this.getId(), gamemode, Dimension.OVERWORLD, Difficulty.PEACEFUL,
                        (short) 1000, LevelType.FLAT),
                new SpawnPositionPacket(floor(x), floor(headY), floor(z)),
                new PlayerAbilitiesPacket(
                        new PlayerAbilities(false, false, true, false, true, 0.5f, 0.1f)),
                new HeldItemChangePacket((short) 0),
                new PlayerPositionAndLookPacket(x, headY, z, yaw, pitch, onGround),
                new SetSlotPacket((byte) 0, (short) 36,
                        new ItemStack(Material.DIAMOND_SWORD, (short) 1, (short) 1)));
        setupAttributes();
        effect(PotionEffect.SPEED, (byte) 1, (short) 32767);
    }

    private static final Attribute[] BASE_ATTRIBUTES = new Attribute[] {
            Attribute.MAX_HEALTH,
            Attribute.KNOCKBACK_RESISTANCE,
            Attribute.MOVEMENT_SPEED,
            Attribute.ATTACK_DAMAGE
    };

    @NotNull
    public AttributeInstance getAttribute(@NotNull Attribute attribute) {
        return attributeModifiers.computeIfAbsent(attribute.getKey(),
                s -> new AttributeInstance(attribute, this::onAttributeChanged));
    }

    private final Map<String, AttributeInstance> attributeModifiers = new ConcurrentHashMap<>();

    protected void onAttributeChanged(@NotNull AttributeInstance attributeInstance) {
        if (attributeInstance.getAttribute().isShared()) {
            boolean self = true; // TODO: is the self player or other players

            if (self) {
                connection.queuePacket(getPropertiesPacket());
                // sendPacketToViewersAndSelf(getPropertiesPacket());
            } else {
                // sendPacketToViewers(getPropertiesPacket());
            }
        }
    }

    protected void setupAttributes() {
        for (val attribute : BASE_ATTRIBUTES) {
            val attributeInstance = new AttributeInstance(attribute, this::onAttributeChanged);
            this.attributeModifiers.put(attribute.getKey(), attributeInstance);
        }
    }

    @NotNull
    // TODO: send properties packet to viewers
    protected EntityPropertiesPacket getPropertiesPacket() {
        // Get all the attributes which should be sent to the client
        val instances = attributeModifiers.values().stream()
                .filter(i -> i.getAttribute().isShared())
                .toArray(AttributeInstance[]::new);

        val properties = new Object2ObjectOpenHashMap<String, Attribute.Property>(instances.length);
        for (int i = 0; i < instances.length; ++i) {
            val property = new Attribute.Property(instances[i].getBaseValue(), instances[i].getModifiers());
            properties.put(instances[i].getAttribute().getKey(), property);
        }
        return new EntityPropertiesPacket(getId(), properties);
    }

    public float getAttributeValue(@NotNull Attribute attribute) {
        val instance = attributeModifiers.get(attribute.getKey());
        return (instance != null) ? instance.getValue() : attribute.getDefaultValue();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlayerImpl player)
            return player.getConnection().equals(getConnection());

        return false;
    }

    @Override
    public int hashCode() {
        return getConnection().hashCode();
    }

    @Override
    public void msg(String message) {
        connection.queuePacket(new ChatMessagePacket(new StringChatComponent(message)));
    }

    @Override
    public MinecraftServerImpl getServer() {
        return server;
    }

}