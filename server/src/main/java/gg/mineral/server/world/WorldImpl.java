package gg.mineral.server.world;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gg.mineral.api.entity.Entity;
import gg.mineral.api.math.MathUtil;
import gg.mineral.api.world.World;
import gg.mineral.api.world.chunk.Chunk;
import gg.mineral.server.MinecraftServerImpl;
import gg.mineral.server.entity.EntityImpl;
import gg.mineral.server.entity.living.human.PlayerImpl;
import gg.mineral.server.network.packet.play.clientbound.MapChunkBulkPacket;
import gg.mineral.server.world.chunk.ChunkImpl;
import gg.mineral.server.world.chunk.EmptyChunkImpl;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class WorldImpl implements World, MathUtil {
    public static final byte MIN_CHUNK_COORD = -127, MAX_CHUNK_COORD = (byte) 128;
    @Getter
    private final byte id;
    @Getter
    private final String name;
    @Getter
    private final Environment environment;
    @Getter
    private final Generator generator;
    @NotNull
    @Getter
    private final MinecraftServerImpl server;
    private final Chunk[] chunkCache = new ChunkImpl[65536];
    private final IntOpenHashSet entities = new IntOpenHashSet();
    private final Int2ShortOpenHashMap entityChunkPositions = new Int2ShortOpenHashMap() {
        {
            defaultReturnValue(Short.MIN_VALUE);
        }
    };

    public Chunk getChunk(short key) {
        var chunk = chunkCache[unsigned(key)];
        byte x = ChunkImpl.xFromKey(key), z = ChunkImpl.zFromKey(key);

        if (chunk == null)
            chunk = chunkCache[unsigned(key)] = generator != null ? generator.generate(this, x, z)
                    : new ChunkImpl(this, x, z);
        return chunk;
    }

    public Chunk setChunk(short key, ChunkImpl chunk) {
        return chunkCache[unsigned(key)] = chunk;
    }

    @Override
    public int getType(int x, short y, int z) {
        return getChunk(ChunkImpl.toKey((byte) (x >> 4), (byte) (z >> 4))).getType(x & 15, z & 15, y);
    }

    @Override
    public int getMetaData(int x, short y, int z) {
        return getChunk(ChunkImpl.toKey((byte) (x >> 4), (byte) (z >> 4))).getMetaData(x & 15, z & 15, y);
    }

    @Override
    @Nullable
    public EntityImpl getEntity(int entityId) {
        return entities.contains(entityId) ? server.getEntities().get(entityId) : null;
    }

    @Override
    @Nullable
    public PlayerImpl getPlayer(int entityId) {
        return entities.contains(entityId) ? server.getPlayers().get(entityId) : null;
    }

    @Override
    public void removeEntity(int id) {
        entities.remove(id);
        short chunkKey = entityChunkPositions.remove(id);
        if (getChunk(chunkKey) instanceof ChunkImpl chunk)
            chunk.getEntities().remove(id);
    }

    @Override
    public void addEntity(Entity entity) {
        val id = entity.getId();
        if (!entities.add(id))
            throw new IllegalStateException("Entity with id " + id + " already exists in world " + name);
        byte chunkX = (byte) Math.floor(entity.getX() / 16);
        byte chunkZ = (byte) Math.floor(entity.getZ() / 16);
        short key = ChunkImpl.toKey(chunkX, chunkZ);
        entityChunkPositions.put(id,
                key);
        if (getChunk(key) instanceof ChunkImpl chunk)
            chunk.getEntities().add(id);
    }

    public void updatePosition(Entity entity) {
        val id = entity.getId();
        byte chunkX = (byte) Math.floor(entity.getX() / 16);
        byte chunkZ = (byte) Math.floor(entity.getZ() / 16);
        short newChunkKey = ChunkImpl.toKey(chunkX, chunkZ);
        short oldChunkKey = entityChunkPositions.put(id,
                newChunkKey);
        if (oldChunkKey != newChunkKey) {
            if (getChunk(oldChunkKey) instanceof ChunkImpl oldChunk)
                oldChunk.getEntities().remove(id);

            if (getChunk(newChunkKey) instanceof ChunkImpl newChunk)
                newChunk.getEntities().add(id);

            entity.setChunkUpdateNeeded(true);
        }
    }

    public List<Chunk> getChunkLoadUpdates(PlayerImpl player) {
        byte viewDistance = player.getViewDistance();
        byte chunkX = (byte) Math.floor(player.getX() / 16), chunkZ = (byte) Math.floor(player.getZ() / 16);
        val chunks = new ArrayList<Chunk>();

        for (int xOffset = -viewDistance; xOffset <= viewDistance; xOffset++) {
            byte cX = (byte) (chunkX + xOffset);
            for (int zOffset = -viewDistance; zOffset <= viewDistance; zOffset++) {
                short key = ChunkImpl.toKey(cX, (byte) (chunkZ + zOffset));
                player.getChunkUpdateTracker().put(key, player.getCurrentTick());
                if (!player.getVisibleChunks().contains(key)) {
                    player.getVisibleChunks().add(key);
                    val chunk = this.getChunk(key);
                    if (chunk != null)
                        chunks.add(chunk);
                }
            }
        }
        return chunks;
    }

    public void updateChunks(PlayerImpl player) {
        player.setChunkUpdateNeeded(false);
        val chunks = getChunkLoadUpdates(player);

        val iterator = player.getChunkUpdateTracker().short2IntEntrySet().fastIterator();

        int currentTick = player.getCurrentTick();

        while (iterator.hasNext()) {
            val entry = iterator.next();
            if (currentTick - entry.getIntValue() > 100) { // linked hashmap to order by eldest entry
                short key = entry.getShortKey();
                if (player.getVisibleChunks().remove(key)) {
                    chunks.add(new EmptyChunkImpl(this, ChunkImpl.xFromKey(key), ChunkImpl.zFromKey(key)));

                    if (getChunk(key) instanceof ChunkImpl impl)
                        for (int playerId : impl.getEntities())
                            if (playerId != this.getId())
                                player.getVisibleEntities().remove(playerId);

                    iterator.remove();
                }
                continue;
            }

            break;
        }

        if (chunks.isEmpty())
            return;

        if (chunks.size() == 1 && chunks.get(0) instanceof ChunkImpl impl)
            player.getConnection().queuePacket(impl.toPacket(true));
        else
            player.getConnection()
                    .queuePacket(new MapChunkBulkPacket(environment == World.Environment.NORMAL, chunks));
    }

}
