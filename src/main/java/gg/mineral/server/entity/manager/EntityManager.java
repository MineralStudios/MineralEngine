package gg.mineral.server.entity.manager;

import java.util.Iterator;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import gg.mineral.server.entity.Entity;
import gg.mineral.server.entity.living.human.Player;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.world.WorldManager;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap.Entry;
import lombok.Getter;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

public class EntityManager {
    @Getter
    static final Int2ObjectOpenHashMap<Entity> entities = new Int2ObjectOpenHashMap<>();
    static int nextEntityId = 0;

    public static void addEntity(Entity entity) {
        entities.put(entity.getId(), entity);
    }

    public static void removeEntity(Entity entity) {
        entities.remove(entity.getId());
    }

    public static int nextEntityId() {
        return nextEntityId++;
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

    public static Player create(Connection connection) {
        remove(p -> p.getName().equals(connection.getName()));
        Player player = new Player(connection, EntityManager.nextEntityId(), WorldManager.getWorld((byte) 0));
        EntityManager.addEntity(player);
        return player;
    }

    public static void iteratePlayers(Consumer<Player> consumer) {
        for (Entity e : entities.values())
            if (e instanceof Player player)
                consumer.accept(player);
    }

    public static void remove(Predicate<Player> predicate) {
        Iterator<Entry<Entity>> iterator = entities.int2ObjectEntrySet().iterator();

        while (iterator.hasNext()) {
            Entry<Entity> entity = iterator.next();

            if (entity instanceof Player player)
                if (predicate.test(player))
                    iterator.remove();
        }
    }
}
