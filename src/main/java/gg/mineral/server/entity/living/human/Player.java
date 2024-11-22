package gg.mineral.server.entity.living.human;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jetbrains.annotations.NotNull;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.chat.component.StringChatComponent;
import gg.mineral.server.MinecraftServer;
import gg.mineral.server.command.CommandExecutor;
import gg.mineral.server.command.impl.KnockbackCommand;
import gg.mineral.server.entity.attribute.Attribute;
import gg.mineral.server.entity.attribute.AttributeInstance;
import gg.mineral.server.entity.attribute.Property;
import gg.mineral.server.entity.effect.PotionEffect;
import gg.mineral.server.entity.living.HumanEntity;
import gg.mineral.server.entity.living.human.property.Gamemode;
import gg.mineral.server.entity.living.human.property.PlayerAbilities;
import gg.mineral.server.entity.metadata.EntityMetadata;
import gg.mineral.server.inventory.item.ItemStack;
import gg.mineral.server.inventory.item.Material;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.login.clientbound.LoginSuccessPacket;
import gg.mineral.server.network.packet.play.bidirectional.AnimationPacket;
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
import gg.mineral.server.network.packet.play.clientbound.EntityStatusPacket;
import gg.mineral.server.network.packet.play.clientbound.EntityVelocityPacket;
import gg.mineral.server.network.packet.play.clientbound.JoinGamePacket;
import gg.mineral.server.network.packet.play.clientbound.PlayerPositionAndLookPacket;
import gg.mineral.server.network.packet.play.clientbound.SetSlotPacket;
import gg.mineral.server.network.packet.play.clientbound.SoundEffectPacket;
import gg.mineral.server.network.packet.play.clientbound.SpawnPlayerPacket;
import gg.mineral.server.network.packet.play.clientbound.SpawnPositionPacket;
import gg.mineral.server.util.collection.GlueList;
import gg.mineral.server.util.math.MathUtil;
import gg.mineral.server.world.IWorld;
import gg.mineral.server.world.property.Difficulty;
import gg.mineral.server.world.property.Dimension;
import gg.mineral.server.world.property.LevelType;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.shorts.Short2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortOpenHashSet;
import it.unimi.dsi.fastutil.shorts.ShortSet;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import gg.mineral.server.network.packet.play.clientbound.EntityTeleportPacket;

public class Player extends HumanEntity implements CommandExecutor {

    @Getter
    private final Short2IntLinkedOpenHashMap chunkUpdateTracker = new Short2IntLinkedOpenHashMap();
    @Getter
    private final ShortSet visibleChunks = new ShortOpenHashSet();
    @Getter
    private final IntSet entityRemoveIds = new IntOpenHashSet();
    @Getter
    private final Int2ObjectOpenHashMap<int[]> visibleEntities = new Int2ObjectOpenHashMap<>() {
        @Override
        public int[] remove(int key) {
            val value = super.remove(key);
            if (value != null)
                entityRemoveIds.add(key);
            return value;
        }
    };
    @Getter
    @Setter
    private int lastDamaged;
    @Getter
    @Setter
    private boolean sprinting;
    @Getter
    @Setter
    private boolean extraKnockback;
    @Getter
    private final Connection connection;
    @Getter
    private IWorld world;
    private final MinecraftServer server;
    @Getter
    private Set<String> permissions = new ObjectOpenHashSet<>();

    @Getter
    @Setter
    private Gamemode gamemode = Gamemode.SURVIVAL;

    public Player(Connection connection, int id, IWorld world) {
        super(id);
        this.setWorld(world);
        this.connection = connection;
        this.server = connection.getServer();
    }

    public String getName() {
        return connection.getName();
    }

    public UUID getUuid() {
        return connection.getUuid();
    }

    public void swingArm() {
        for (int id : visibleEntities.keySet()) {
            val player = server.getEntityManager().getPlayer(id);

            if (player == null)
                continue;

            player.updateArm(this);
        }
    }

    public void effect(PotionEffect effect, byte amplifier, short duration) {
        effect.applyAttributes(this, amplifier, duration);
        connection.queuePacket(new EntityEffectPacket(id, PotionEffect.SPEED.getId(), amplifier, duration));
    }

    public void disconnect(BaseChatComponent chatComponent) {
        getConnection().disconnect(chatComponent);
    }

    public void setWorld(IWorld world) {
        val oldWorld = getWorld();

        if (oldWorld != null)
            oldWorld.removeEntity(this.getId());
        world.addEntity(this);
        this.world = world;
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

            int x = MathUtil.toFixedPointInt(entity.getX()), y = MathUtil.toFixedPointInt(entity.getY()),
                    z = MathUtil.toFixedPointInt(entity.getZ());
            byte yaw = MathUtil.angleToByte(entity.getYaw()),
                    pitch = MathUtil.angleToByte(entity.getPitch());
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
            val newlyVisible = world.getChunk(key).getEntities();

            for (int entityId : newlyVisible) {
                if (entityId == this.getId())
                    continue;

                val player = world.getPlayer(entityId);

                if (player == null)
                    throw new IllegalStateException("Entity with id " + entityId + " is not a player");

                if (visibleEntities.containsKey(entityId))
                    continue;

                final int x = MathUtil.toFixedPointInt(player.getX()),
                        y = MathUtil.toFixedPointInt(player.getY()),
                        z = MathUtil.toFixedPointInt(player.getZ());
                byte yaw = MathUtil.angleToByte(player.getYaw()),
                        pitch = MathUtil.angleToByte(player.getPitch());
                connection.sendPacket(new SpawnPlayerPacket(player.getId(), // TODO: Fix not being able to see player
                        // sometimes
                        x, y, z, yaw,
                        pitch, player.getUuid().toString(),
                        player.getName(), new GlueList<>() /* TODO: player property */, (short) 0 /*
                                                                                                   * TODO:
                                                                                                   * held
                                                                                                   * item
                                                                                                   */,
                        new EntityMetadata(Player.class).getEntryList()/* TODO: entity metadata */));
                visibleEntities.put(player.getId(), new int[] { x, y, z, yaw, pitch });
            }
        }
    }

    boolean swingingArm;
    int swingingTicks;

    public void updateArm(Player player) {
        if (!player.swingingArm || player.swingingTicks >= player.swingSpeed() / 2 || player.swingingTicks < 0) {
            player.swingingTicks = -1;
            player.swingingArm = true;

            getConnection().queuePacket(new AnimationPacket(player.getId(), (short) 0));
        }

    }

    private int swingSpeed() {
        /*
         * return this.hasEffect(MobEffectList.FASTER_DIG)
         * ? 6 - (1 + this.getEffect(MobEffectList.FASTER_DIG).getAmplifier()) * 1
         * : (this.hasEffect(MobEffectList.SLOWER_DIG)
         * ? 6 + (1 + this.getEffect(MobEffectList.SLOWER_DIG).getAmplifier()) * 2
         * : 6);
         */
        return 6;
    }

    protected void tickArm() {
        int i = this.swingSpeed();

        if (this.swingingArm) {
            ++this.swingingTicks;
            if (this.swingingTicks >= i) {
                this.swingingTicks = 0;
                this.swingingArm = false;
            }
        } else {
            this.swingingTicks = 0;
        }

        // this.az = (float) this.as / (float) i;
    }

    public void onJoin() {
        this.setY(70);
        connection.queuePacket(new LoginSuccessPacket(connection.getUuid(), connection.getName()),
                new JoinGamePacket(this.getId(), gamemode, Dimension.OVERWORLD, Difficulty.PEACEFUL,
                        (short) 1000, LevelType.FLAT),
                new SpawnPositionPacket(MathUtil.floor(x), MathUtil.floor(y), MathUtil.floor(z)),
                new PlayerAbilitiesPacket(
                        new PlayerAbilities(false, false, false, false, true, 0.05f, 0.1f)),
                new HeldItemChangePacket((short) 0),
                new PlayerPositionAndLookPacket(x, y, z, yaw, pitch, onGround),
                new SetSlotPacket((byte) 0, (short) 36, new ItemStack(Material.DIAMOND_SWORD, (short) 1, (short) 1)));
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
        for (Attribute attribute : BASE_ATTRIBUTES) {
            final AttributeInstance attributeInstance = new AttributeInstance(attribute, this::onAttributeChanged);
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

        val properties = new Object2ObjectOpenHashMap<String, Property>(instances.length);
        for (int i = 0; i < instances.length; ++i) {
            val property = new Property(instances[i].getBaseValue(), instances[i].getModifiers());
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
        if (obj instanceof Player player)
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
    public MinecraftServer getServer() {
        return server;
    }

    public void attack(int target) {
        val entityManager = server.getEntityManager();
        val entity = entityManager.getEntity(target);
        if (entity instanceof Player player && player.getCurrentTick() - player.getLastDamaged() >= 10) {
            player.setLastDamaged(player.getCurrentTick());
            val statusPacket = new EntityStatusPacket(player.getId(), (byte) 2);

            double motX = player.getMotX();
            double motY = player.getMotY();
            double motZ = player.getMotZ();
            double x = KnockbackCommand.x;
            double y = KnockbackCommand.y;
            double z = KnockbackCommand.z;

            double extraX = KnockbackCommand.extraX;
            double extraY = KnockbackCommand.extraY;
            double extraZ = KnockbackCommand.extraZ;

            double yLimit = KnockbackCommand.yLimit;

            double friction = KnockbackCommand.friction;

            if (friction > 0) {
                motX /= friction;
                motY /= friction;
                motZ /= friction;
            } else {
                motX = 0;
                motY = 0;
                motZ = 0;
            }

            double distanceX = this.getX() - player.getX();
            double distanceZ = this.getZ() - player.getZ();

            double magnitude = Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);

            motX -= distanceX / magnitude * x;
            motY += y;
            motZ -= distanceZ / magnitude * z;

            if (motY > yLimit)
                motY = yLimit;

            if (this.isExtraKnockback()) {
                float angle = (float) Math.toRadians(this.getYaw());
                double sin = -Math.sin(angle);
                double cos = Math.cos(angle);
                motX += extraX * sin;
                motY += extraY;
                motZ += extraZ * cos;
                this.setMotX(this.getMotX() * 0.6);
                this.setMotZ(this.getMotZ() * 0.6);
                this.setExtraKnockback(false);
            }
            val velocityPacket = new EntityVelocityPacket(target, MathUtil.toVelocityUnits(motX),
                    MathUtil.toVelocityUnits(motY), MathUtil.toVelocityUnits(motZ));
            player.getConnection().queuePacket(statusPacket, velocityPacket);
            connection.queuePacket(statusPacket, new SoundEffectPacket("game.player.hurt",
                    MathUtil.toSoundUnits(player.getX()), MathUtil.toSoundUnits(player.getY()),
                    MathUtil.toSoundUnits(player.getZ()), 1.0f, MathUtil.toPitchUnits((this.getRandom().nextFloat()
                            - this.getRandom().nextFloat()) * 0.2F + 1.0F)));
            player.setMotX(motX);
            player.setMotY(motY);
            player.setMotZ(motZ);
        }
    }

}