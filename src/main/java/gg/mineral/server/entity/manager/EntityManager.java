package gg.mineral.server.entity.manager;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import gg.mineral.server.entity.Entity;
import gg.mineral.server.entity.living.human.Player;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.util.collection.ConcurrentHashSet;
import gg.mineral.server.util.collection.NonBlockingHashMap;
import gg.mineral.server.world.World;
import gg.mineral.server.world.WorldManager;
import lombok.Getter;

public class EntityManager {

    @Getter
    static final NonBlockingHashMap<Integer, Entity> entities = new NonBlockingHashMap<>();
    @Getter
    static final NonBlockingHashMap<Short, ConcurrentHashSet<Integer>> chunkPosToEntityMap = new NonBlockingHashMap<>();
    static AtomicInteger nextEntityId = new AtomicInteger(0);

    public static void addEntity(Entity entity) {
        entities.put(entity.getId(), entity);
    }

    public static void removeEntity(Entity entity) {
        entities.remove(entity.getId());
    }

    public static int nextEntityId() {
        return nextEntityId.getAndIncrement();
    }

    public static Optional<Entity> getEntity(int id) {
        return Optional.ofNullable(entities.get(id));
    }

    public static Optional<Player> get(Predicate<Player> predicate) {
        for (Entity e : entities.values())
            if (e instanceof Player player)
                if (predicate.test(player))
                    return Optional.of(player);

        return Optional.empty();
    }

    public static Optional<Player> get(Connection connection) {
        return get(connection.getEntityId()).map(p -> (Player) p);
    }

    public static Optional<Entity> get(int entityId) {
        return Optional.ofNullable(entities.get(entityId));
    }

    public static Optional<Player> getPlayer(int entityId) {
        return get(entityId).map(p -> (Player) p);
    }

    public static Player create(Connection connection) {
        remove(p -> p.getName().equals(connection.getName()));
        World spawnWorld = WorldManager.getWorld((byte) 0);
        Player player = new Player(connection, EntityManager.nextEntityId(), spawnWorld);
        connection.setEntityId(player.getId());
        EntityManager.addEntity(player);
        return player;
    }

    public static void iteratePlayers(Consumer<Player> consumer) {
        for (Entity e : entities.values())
            if (e instanceof Player player)
                consumer.accept(player);
    }

    public static void remove(Predicate<Player> predicate) {
        Iterator<Entry<Integer, Entity>> iterator = entities.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<Integer, Entity> entity = iterator.next();

            if (entity instanceof Player player)
                if (predicate.test(player))
                    iterator.remove();
        }
    }
}
