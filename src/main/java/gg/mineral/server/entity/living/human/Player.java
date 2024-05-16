package gg.mineral.server.entity.living.human;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.chat.component.StringChatComponent;
import gg.mineral.server.command.CommandExecutor;
import gg.mineral.server.entity.attribute.Property;
import gg.mineral.server.entity.living.HumanEntity;
import gg.mineral.server.entity.living.human.property.Gamemode;
import gg.mineral.server.entity.living.human.property.PlayerAbilities;
import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.entity.metadata.EntityMetadata;
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
import gg.mineral.server.network.packet.play.clientbound.JoinGamePacket;
import gg.mineral.server.network.packet.play.clientbound.MapChunkBulkPacket;
import gg.mineral.server.network.packet.play.clientbound.PlayerPositionAndLookPacket;
import gg.mineral.server.network.packet.play.clientbound.SpawnPlayerPacket;
import gg.mineral.server.network.packet.play.clientbound.SpawnPositionPacket;
import gg.mineral.server.util.collection.ConcurrentHashSet;
import gg.mineral.server.util.collection.GlueList;
import gg.mineral.server.util.math.MathUtil;
import gg.mineral.server.world.World;
import gg.mineral.server.world.chunk.Chunk;
import gg.mineral.server.world.chunk.EmptyChunk;
import gg.mineral.server.world.property.Difficulty;
import gg.mineral.server.world.property.Dimension;
import gg.mineral.server.world.property.LevelType;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.Short2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2IntMap.Entry;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import lombok.Getter;
import lombok.Setter;

public class Player extends HumanEntity implements CommandExecutor {

    @Getter
    final Short2IntLinkedOpenHashMap loadedChunks = new Short2IntLinkedOpenHashMap();
    @Getter
    final ShortArrayList visibleChunks = new ShortArrayList();
    @Getter
    final Int2ObjectOpenHashMap<int[]> visibleEntities = new Int2ObjectOpenHashMap<>();
    @Getter
    private byte chunkX, chunkZ, oldChunkX, oldChunkZ;
    @Getter
    @Setter
    private boolean firstTick = true;
    @Getter
    @Setter
    private int lastDamaged;
    @Getter
    private final byte viewDistance = (byte) 10;
    @Getter
    @Setter
    private long tickNanoTime;
    @Getter
    boolean sprinting;
    @Getter
    @Setter
    boolean extraKnockback;

    @Getter
    final Connection connection;
    @Setter
    @Getter
    World world;
    @Getter
    final IntList entityRemoveIds = new IntArrayList();

    final Object lock = new Object();

    @Getter
    @Setter
    Gamemode gamemode = Gamemode.SURVIVAL;

    final Property movementSpeed = new Property(0.1, 0.1, null);

    public Player(Connection connection, int id, World world) {
        super(id);
        this.connection = connection;
        this.world = world;
    }

    public String getName() {
        return connection.getName();
    }

    public UUID getUuid() {
        return connection.getUuid();
    }

    public void setSprinting(boolean sprint) {

        if (sprint && !sprinting) {
            movementSpeed.setValue(movementSpeed.getValue() * 1.3);
        } else if (!sprint && sprinting) {
            movementSpeed.setValue(movementSpeed.getValue() / 1.3);
        }

        this.sprinting = sprint;
    }

    public void effectSpeed() {
        movementSpeed.setValue(movementSpeed.getValue() * 1.4);
        connection.queuePacket(new EntityEffectPacket(id, (byte) 1, (byte) 1, (short) 32767));
    }

    public void disconnect(BaseChatComponent chatComponent) {
        getConnection().disconnect(chatComponent);
    }

    public void updateChunkPosition() {
        this.oldChunkX = chunkX;
        this.oldChunkZ = chunkZ;
        this.chunkX = (byte) Math.floor(getX() / 16);
        this.chunkZ = (byte) Math.floor(getZ() / 16);

        if (oldChunkX != chunkX || oldChunkZ != chunkZ)
            EntityManager.getChunkPosToEntityMap().get(Chunk.toKey(oldChunkX, oldChunkZ)).remove(this.getId());
        EntityManager.getChunkPosToEntityMap()
                .computeIfAbsent(Chunk.toKey(chunkX, chunkZ), k -> new ConcurrentHashSet<Integer>())
                .add(this.getId());
    }

    @Override
    public void tick() {
        synchronized (lock) {
            super.tick();
            updateChunkPosition();

            if (oldChunkX != chunkX || oldChunkZ != chunkZ || isFirstTick())
                sendUpdates();

            updateVisibleEntities();

            tickArm();

            updateProperties();

            setFirstTick(false);
        }
    }

    public void updateVisibleEntities() {

        if (isFirstTick())
            return;

        for (short key : visibleChunks) {

            ConcurrentHashSet<Integer> newlyVisible = EntityManager.getChunkPosToEntityMap().get(key);

            if (newlyVisible == null)
                continue;

            Iterator<Integer> iterator = newlyVisible.iterator();

            while (iterator.hasNext()) {
                int playerId = iterator.next();

                if (playerId == this.getId())
                    continue;

                Optional<Player> playerOptional = EntityManager.getPlayer(playerId);

                if (visibleEntities.containsKey(playerId)) {

                    if (!playerOptional.isPresent() || !playerOptional.get().getWorld().equals(getWorld())) {
                        visibleEntities.remove(playerId);
                        getEntityRemoveIds().add(playerId);
                        iterator.remove();
                    }

                    continue;
                }

                playerOptional.ifPresent(player -> {
                    if (player.isFirstTick())
                        return;
                    int x = MathUtil.toFixedPointInt(player.getX()),
                            y = MathUtil.toFixedPointInt(player.getY()),
                            z = MathUtil.toFixedPointInt(player.getZ()),
                            yaw = MathUtil.angleToByte(player.getYaw()),
                            pitch = MathUtil.angleToByte(player.getPitch());
                    visibleEntities.put(player.getId(), new int[] { x, y, z, yaw, pitch });
                    getConnection().queuePacket(new SpawnPlayerPacket(player.getId(),
                            x, y, z, player.getYaw(),
                            player.getPitch(), player.getUuid().toString(),
                            player.getName(), new GlueList<>() /* TODO: player property */, (short) 0 /*
                                                                                                       * TODO:
                                                                                                       * held
                                                                                                       * item
                                                                                                       */,
                            new EntityMetadata(Player.class).getEntryList()/* TODO: entity metadata */));
                });

            }
        }

        for (it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry<int[]> e : visibleEntities.int2ObjectEntrySet()) {

            EntityManager.getEntity(e.getIntKey()).ifPresent(key -> {
                if (key instanceof Player player) {

                    int[] prevLoc = e.getValue();
                    int x = MathUtil.toFixedPointInt(player.getX()), y = MathUtil.toFixedPointInt(player.getY()),
                            z = MathUtil.toFixedPointInt(player.getZ());
                    byte yaw = MathUtil.angleToByte(player.getYaw()),
                            pitch = MathUtil.angleToByte(player.getPitch());
                    byte deltaX = (byte) (x - prevLoc[0]), deltaY = (byte) (y - prevLoc[1]),
                            deltaZ = (byte) (z - prevLoc[2]), deltaYaw = (byte) (yaw - prevLoc[3]),
                            deltaPitch = (byte) (pitch - prevLoc[4]);

                    boolean moved = deltaX != 0 || deltaY != 0 || deltaZ != 0,
                            rotated = Math.abs(deltaYaw) >= 1 || Math.abs(deltaPitch) >= 1;
                    if (moved && rotated)
                        getConnection().queuePacket(new EntityLookAndRelativeMovePacket(player.getId(),
                                deltaX,
                                deltaY, deltaZ,
                                yaw, pitch),
                                new EntityHeadLookPacket(player.getId(), yaw));
                    else if (moved)
                        getConnection().queuePacket(new EntityRelativeMovePacket(player.getId(),
                                deltaX,
                                deltaY, deltaZ));
                    else if (rotated)
                        getConnection().queuePacket(new EntityLookPacket(player.getId(),
                                yaw, pitch),
                                new EntityHeadLookPacket(player.getId(), yaw));

                    if (moved || rotated)
                        e.setValue(new int[] { x, y, z, yaw, pitch });

                }
            });
        }

        if (getEntityRemoveIds().isEmpty())
            return;
        int[] ids = getEntityRemoveIds().toIntArray();
        getEntityRemoveIds().clear();

        getConnection().queuePacket(new DestroyEntitiesPacket(ids));
    }

    public List<Chunk> getChunkLoadUpdates() {
        byte viewDistance = getViewDistance();
        int chunkX = getChunkX();
        int chunkZ = getChunkZ();
        List<Chunk> chunks = new GlueList<>();

        for (int xOffset = -viewDistance; xOffset <= viewDistance; xOffset++) {
            byte cX = (byte) (chunkX + xOffset);
            for (int zOffset = -viewDistance; zOffset <= viewDistance; zOffset++) {
                short key = Chunk.toKey(cX, (byte) (chunkZ + zOffset));
                Chunk chunk = createChunkUpdate(key);
                if (chunk != null)
                    chunks.add(chunk);
            }
        }
        return chunks;
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

    public Chunk createChunkUpdate(short key) {
        loadedChunks.put(key, getCurrentTick());
        ShortArrayList visibleChunks = getVisibleChunks();
        if (visibleChunks.contains(key))
            return null;

        Chunk chunk = getWorld().getChunk(key);
        visibleChunks.add(key);
        return chunk;
    }

    public void sendUpdates() {
        List<Chunk> chunks = getChunkLoadUpdates();

        ObjectIterator<Entry> iterator = loadedChunks.short2IntEntrySet().fastIterator();
        World world = getWorld();

        int currentTick = getCurrentTick();
        ShortArrayList visibleChunks = getVisibleChunks();

        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (currentTick - entry.getIntValue() > 100) { // linked hashmap to order by eldest entry
                short key = entry.getShortKey();
                if (visibleChunks.rem(key)) {
                    chunks.add(new EmptyChunk(world, Chunk.xFromKey(key), Chunk.zFromKey(key)));
                    ConcurrentHashSet<Integer> newlyInvisible = EntityManager.getChunkPosToEntityMap().get(key);

                    if (newlyInvisible != null) {
                        for (int playerId : newlyInvisible) {
                            if (playerId == this.getId())
                                continue;

                            getEntityRemoveIds().add(playerId);
                            visibleEntities.remove(playerId);
                        }
                    }

                    iterator.remove();
                }
                continue;
            }

            break;
        }

        if (chunks.isEmpty()) {
            return;
        }

        Connection connection = getConnection();
        if (chunks.size() == 1) {
            connection.queuePacket(chunks.get(0).toPacket());
        } else {
            connection.queuePacket(new MapChunkBulkPacket(world.getEnvironment() == World.Environment.NORMAL, chunks));
        }
    }

    public void onJoin() {
        connection.sendPacket(
                new LoginSuccessPacket(connection.getUuid(), connection.getName()),
                new JoinGamePacket(this.getId(), gamemode, Dimension.OVERWORLD, Difficulty.PEACEFUL,
                        LevelType.FLAT, (short) 1000));

        connection.queuePacket(
                new SpawnPositionPacket(0, 70, 0),
                new PlayerAbilitiesPacket(
                        new PlayerAbilities(false, false, false, false, true, 0.05f, 0.1f)),
                new HeldItemChangePacket((short) 0),
                new PlayerPositionAndLookPacket(0, 70, 0, 0f, 0f, false));

        effectSpeed();
    }

    public void updateProperties() {
        if (movementSpeed.getValue() != movementSpeed.getLastValue()) {
            Map<String, Property> properties = new HashMap<>();
            properties.put("generic.movementSpeed", movementSpeed);
            connection.queuePacket(new EntityPropertiesPacket(id, properties));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Player))
            return false;

        Player player = (Player) obj;

        return player.getConnection().equals(getConnection());
    }

    @Override
    public int hashCode() {
        return getConnection().hashCode();
    }

    @Override
    public void msg(String message) {
        connection.queuePacket(new ChatMessagePacket(new StringChatComponent(message)));
    }
}