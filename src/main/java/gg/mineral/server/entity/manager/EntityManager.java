package gg.mineral.server.entity.manager;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import gg.mineral.server.entity.Entity;
import gg.mineral.server.entity.living.human.Player;
import gg.mineral.server.network.connection.Connection;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

import gg.mineral.server.MinecraftServer;

@RequiredArgsConstructor
public class EntityManager {
    @Getter
    private final Int2ObjectOpenHashMap<Entity> entities = new Int2ObjectOpenHashMap<>();
    private final MinecraftServer server;
    private int nextEntityId = 0;

    public void addEntity(Entity entity) {
        entities.put(entity.getId(), entity);
    }

    public int nextEntityId() {
        return nextEntityId++;
    }

    @Nullable
    public Entity getEntity(int id) {
        return entities.get(id);
    }

    @Nullable
    public Player get(Predicate<Player> predicate) {
        for (val e : entities.values())
            if (e instanceof Player player)
                if (predicate.test(player))
                    return player;

        return null;
    }

    @Nullable
    public Player get(Connection connection) {
        return get(player -> player.getConnection() == connection);
    }

    @Nullable
    public Entity get(int entityId) {
        return entities.get(entityId);
    }

    @Nullable
    public Player getPlayer(int entityId) {
        val entity = get(entityId);

        if (entity instanceof Player player)
            return player;

        return null;
    }

    public Player create(Connection connection) {
        val spawnWorld = server.getWorldManager().getWorld((byte) 0);
        val player = new Player(connection, this.nextEntityId(), spawnWorld);
        this.addEntity(player);
        return player;
    }

    public void iteratePlayers(Consumer<Player> consumer) {
        for (val e : entities.values())
            if (e instanceof Player player)
                consumer.accept(player);
    }

    public void remove(int id) {
        entities.remove(id);
    }

    public void remove(Connection connection) {
        val player = get(connection);
        if (player != null)
            remove(player.getId());
    }
}
