package gg.mineral.server.world;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import gg.mineral.server.entity.Entity;
import gg.mineral.server.entity.living.human.Player;
import gg.mineral.server.network.packet.play.clientbound.MapChunkBulkPacket;
import gg.mineral.server.util.collection.GlueList;
import gg.mineral.server.util.math.MathUtil;
import gg.mineral.server.world.chunk.Chunk;
import gg.mineral.server.world.chunk.EmptyChunk;
import it.unimi.dsi.fastutil.ints.Int2ShortOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import gg.mineral.server.MinecraftServer;

@RequiredArgsConstructor
public class World implements IWorld {
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
    private final MinecraftServer server;
    private final Chunk[] chunkCache = new Chunk[65536];
    private final IntOpenHashSet entities = new IntOpenHashSet();
    private final Int2ShortOpenHashMap entityChunkPositions = new Int2ShortOpenHashMap() {
        {
            defaultReturnValue(Short.MIN_VALUE);
        }
    };

    public Chunk getChunk(short key) {
        var chunk = chunkCache[MathUtil.unsigned(key)];
        byte x = Chunk.xFromKey(key), z = Chunk.zFromKey(key);

        if (chunk == null)
            chunk = chunkCache[MathUtil.unsigned(key)] = generator != null ? generator.generate(this, x, z)
                    : new EmptyChunk(this, x, z);
        return chunk;
    }

    @Override
    public int getType(int x, short y, int z) {
        return getChunk(Chunk.toKey((byte) (x >> 4), (byte) (z >> 4))).getType(x & 15, z & 15, y);
    }

    @Override
    public int getMetaData(int x, short y, int z) {
        return getChunk(Chunk.toKey((byte) (x >> 4), (byte) (z >> 4))).getMetaData(x & 15, z & 15, y);
    }

    @Override
    @Nullable
    public Entity getEntity(int entityId) {
        boolean contains = entities.contains(entityId);
        return contains ? server.getEntityManager().getEntity(entityId) : null;
    }

    @Override
    @Nullable
    public Player getPlayer(int entityId) {
        if (entities.contains(entityId) && server.getEntityManager().getEntity(entityId) instanceof Player player)
            return player;

        return null;
    }

    @Override
    public void removeEntity(int id) {
        entities.remove(id);
        short chunkKey = entityChunkPositions.remove(id);
        getChunk(chunkKey).getEntities().remove(id);
    }

    @Override
    public void addEntity(Entity entity) {
        val id = entity.getId();
        if (!entities.add(id))
            throw new IllegalStateException("Entity with id " + id + " already exists in world " + name);
        byte chunkX = (byte) Math.floor(entity.getX() / 16);
        byte chunkZ = (byte) Math.floor(entity.getZ() / 16);
        short key = Chunk.toKey(chunkX, chunkZ);
        entityChunkPositions.put(id,
                key);
        getChunk(key).getEntities().add(id);
    }

    @Override
    public void updatePosition(Entity entity) {
        val id = entity.getId();
        byte chunkX = (byte) Math.floor(entity.getX() / 16);
        byte chunkZ = (byte) Math.floor(entity.getZ() / 16);
        short newChunkKey = Chunk.toKey(chunkX, chunkZ);
        short oldChunkKey = entityChunkPositions.put(id,
                newChunkKey);
        if (oldChunkKey != newChunkKey) {
            getChunk(oldChunkKey).getEntities().remove(id);
            getChunk(newChunkKey).getEntities().add(id);
            entity.setChunkUpdateNeeded(true);
        }
    }

    @Override
    public List<Chunk> getChunkLoadUpdates(Player player) {
        byte viewDistance = player.getViewDistance();
        byte chunkX = (byte) Math.floor(player.getX() / 16), chunkZ = (byte) Math.floor(player.getZ() / 16);
        val chunks = new GlueList<Chunk>();

        for (int xOffset = -viewDistance; xOffset <= viewDistance; xOffset++) {
            byte cX = (byte) (chunkX + xOffset);
            for (int zOffset = -viewDistance; zOffset <= viewDistance; zOffset++) {
                short key = Chunk.toKey(cX, (byte) (chunkZ + zOffset));
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

    @Override
    public void updateChunks(Player player) {
        player.setChunkUpdateNeeded(false);
        val chunks = getChunkLoadUpdates(player);

        val iterator = player.getChunkUpdateTracker().short2IntEntrySet().fastIterator();

        int currentTick = player.getCurrentTick();

        while (iterator.hasNext()) {
            val entry = iterator.next();
            if (currentTick - entry.getIntValue() > 100) { // linked hashmap to order by eldest entry
                short key = entry.getShortKey();
                if (player.getVisibleChunks().remove(key)) {
                    chunks.add(new EmptyChunk(this, Chunk.xFromKey(key), Chunk.zFromKey(key)));
                    val newlyInvisible = getChunk(key).getEntities();

                    for (int playerId : newlyInvisible) {
                        if (playerId == this.getId())
                            continue;

                        player.getVisibleEntities().remove(playerId);
                    }

                    iterator.remove();
                }
                continue;
            }

            break;
        }

        if (chunks.isEmpty())
            return;

        if (chunks.size() == 1)
            player.getConnection().queuePacket(chunks.get(0).toPacket(true));
        else
            player.getConnection()
                    .queuePacket(new MapChunkBulkPacket(environment == IWorld.Environment.NORMAL, chunks));
    }

}
