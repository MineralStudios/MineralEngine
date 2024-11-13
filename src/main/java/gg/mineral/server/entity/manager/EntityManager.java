package gg.mineral.server.entity.manager;

import java.util.function.Consumer;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;

import gg.mineral.server.entity.Entity;
import gg.mineral.server.entity.living.human.Player;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.world.World;
import gg.mineral.server.world.WorldManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;

public class EntityManager {
    @Getter
    private static final Int2ObjectOpenHashMap<Entity> entities = new Int2ObjectOpenHashMap<>();
    private static int nextEntityId = 0;

    public static void addEntity(Entity entity) {
        entities.put(entity.getId(), entity);
    }

    public static int nextEntityId() {
        return nextEntityId++;
    }

    @Nullable
    public static Entity getEntity(int id) {
        return entities.get(id);
    }

    @Nullable
    public static Player get(Predicate<Player> predicate) {
        for (Entity e : entities.values())
            if (e instanceof Player player)
                if (predicate.test(player))
                    return player;

        return null;
    }

    @Nullable
    public static Player get(Connection connection) {
        return get(player -> player.getConnection() == connection);
    }

    @Nullable
    public static Entity get(int entityId) {
        return entities.get(entityId);
    }

    @Nullable
    public static Player getPlayer(int entityId) {
        val entity = get(entityId);

        if (entity instanceof Player player)
            return player;

        return null;
    }

    public static Player create(Connection connection) {
        World spawnWorld = WorldManager.getWorld((byte) 0);
        Player player = new Player(connection, EntityManager.nextEntityId(), spawnWorld);
        EntityManager.addEntity(player);
        return player;
    }

    public static void iteratePlayers(Consumer<Player> consumer) {
        for (val e : entities.values())
            if (e instanceof Player player)
                consumer.accept(player);
    }

    public static void remove(int id) {
        entities.remove(id);
    }

    public static void remove(Connection connection) {
        val player = get(connection);
        if (player != null)
            remove(player.getId());
    }
}
