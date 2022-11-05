package gg.mineral.server.entity;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.util.collection.GlueList;

public class PlayerManager {
    public static List<Player> LIST = new GlueList<Player>();

    public static Player get(Predicate<Player> predicate) {
        for (Player player : LIST) {
            if (predicate.test(player)) {
                return player;
            }
        }

        return null;
    }

    public static Player create(String name, Connection connection) {
        remove(p -> p.getName().equals(name));
        Player player = new Player(name, connection);
        LIST.add(player);
        return player;
    }

    public static void remove(Predicate<Player> predicate) {
        Iterator<Player> iterator = LIST.iterator();

        while (iterator.hasNext()) {
            Player player = iterator.next();

            if (predicate.test(player)) {
                iterator.remove();
            }
        }
    }

    public static void disconnect(Player player, BaseChatComponent chatComponent) {
        // player.getConnection().state = MinecraftProtocol.PLAY;
        // LIST.remove(player);
        // player.getConnection().send(new
        // ServerPlayDisconnectPacket(chatComponent.getFormattedText()));
        // player.getConnection().close(chatComponent);
    }
}
