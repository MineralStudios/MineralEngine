package gg.mineral.server.entity.living.human;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import dev.zerite.craftlib.chat.component.StringChatComponent;
import gg.mineral.server.MinecraftServer;
import gg.mineral.server.command.CommandExecutor;
import gg.mineral.server.entity.living.HumanEntity;
import gg.mineral.server.entity.living.human.property.Gamemode;
import gg.mineral.server.entity.living.human.property.PlayerAbilities;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.login.clientbound.LoginSuccessPacket;
import gg.mineral.server.network.packet.play.bidirectional.HeldItemChangePacket;
import gg.mineral.server.network.packet.play.bidirectional.PlayerAbilitiesPacket;
import gg.mineral.server.network.packet.play.clientbound.ChatMessagePacket;
import gg.mineral.server.network.packet.play.clientbound.JoinGamePacket;
import gg.mineral.server.network.packet.play.clientbound.MapChunkBulkPacket;
import gg.mineral.server.network.packet.play.clientbound.PlayerPositionAndLookPacket;
import gg.mineral.server.network.packet.play.clientbound.SpawnPositionPacket;
import gg.mineral.server.util.collection.GlueList;
import gg.mineral.server.world.World;
import gg.mineral.server.world.chunk.Chunk;
import gg.mineral.server.world.chunk.EmptyChunk;
import gg.mineral.server.world.property.Difficulty;
import gg.mineral.server.world.property.Dimension;
import gg.mineral.server.world.property.LevelType;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.shorts.Short2IntMap.Entry;
import it.unimi.dsi.fastutil.shorts.Short2IntOpenHashMap;
import it.unimi.dsi.fastutil.shorts.ShortArrayList;
import lombok.Getter;
import lombok.Setter;

public class Player extends HumanEntity implements CommandExecutor {

    @Getter
    final Short2IntOpenHashMap loadedChunks = new Short2IntOpenHashMap();
    @Getter
    final ShortArrayList visibleChunks = new ShortArrayList();
    @Getter
    private byte chunkX, chunkZ, oldChunkX, oldChunkZ;
    @Getter
    @Setter
    private boolean firstTick = true;
    @Getter
    private final byte viewDistance = (byte) 10;
    @Getter
    @Setter
    private long tickNanoTime;

    @Getter
    final Connection connection;
    @Setter
    @Getter
    World world;

    @Getter
    @Setter
    Gamemode gamemode = Gamemode.SURVIVAL;

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

    public void disconnect(BaseChatComponent chatComponent) {
        getConnection().disconnect(chatComponent);
    }

    public void updateChunkPosition() {
        this.oldChunkX = chunkX;
        this.oldChunkZ = chunkZ;
        this.chunkX = (byte) Math.floor(getX() / 16);
        this.chunkZ = (byte) Math.floor(getZ() / 16);
    }

    public void tick() {

        super.tick();
        updateChunkPosition();

        if (getOldChunkX() != getChunkX() || getOldChunkZ() != getChunkZ()
                || isFirstTick())
            sendUpdates();

        // Tick connection last
        getConnection().tick();

        setFirstTick(false);
    }

    public List<Chunk> getChunkLoadUpdates() {

        byte viewDistance = getViewDistance();
        List<Chunk> chunks = new GlueList<>();

        for (byte x = (byte) -viewDistance; x <= viewDistance; x++)
            for (byte z = (byte) -viewDistance; z <= viewDistance; z++)
                createChunkUpdate(Chunk.toKey((byte) (getChunkX() + x), (byte) (getChunkZ() + z)))
                        .ifPresent(chunks::add);

        return chunks;
    }

    public Optional<Chunk> createChunkUpdate(short key) {
        loadedChunks.put(key, MinecraftServer.getTickLoop().getCurrentTick());
        if (getVisibleChunks().contains(key))
            return Optional.empty();

        Chunk chunk = getWorld().getChunk(key);
        getVisibleChunks().add(key);
        return Optional.of(chunk);
    }

    public void sendUpdates() {
        List<Chunk> chunks = getChunkLoadUpdates();

        ObjectIterator<Entry> iterator = loadedChunks.short2IntEntrySet().fastIterator();

        World world = getWorld();

        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (MinecraftServer.getTickLoop().getCurrentTick() - entry.getIntValue() > 100) {
                short key = entry.getShortKey();
                if (getVisibleChunks().rem(key)) {
                    chunks.add(new EmptyChunk(world, Chunk.xFromKey(key), Chunk.zFromKey(key)));
                    iterator.remove();
                }
            }
        }

        if (chunks.isEmpty())
            return;

        if (chunks.size() == 1)
            getConnection().queuePacket(chunks.get(0).toPacket());
        else
            getConnection()
                    .queuePacket(new MapChunkBulkPacket(world.getEnvironment() == World.Environment.NORMAL,
                            chunks));
    }

    public void onJoin() {
        connection.sendPacket(
                new LoginSuccessPacket(connection.getUuid(), connection.getName()),
                new JoinGamePacket(this.getId(), gamemode, Dimension.OVERWORLD, Difficulty.PEACEFUL,
                        LevelType.FLAT, (short) 1000),
                new SpawnPositionPacket(0, 70, 0),
                new PlayerAbilitiesPacket(
                        new PlayerAbilities(false, false, true, true, true, 0.05f, 0.1f)),
                new HeldItemChangePacket((short) 0),
                new PlayerPositionAndLookPacket(0, 70, 0, 0f, 0f, false));
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

    public void msg(String message) {
        connection.queuePacket(new ChatMessagePacket(new StringChatComponent(message)));
    }
}
