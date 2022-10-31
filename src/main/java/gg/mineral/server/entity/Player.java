package gg.mineral.server.entity;

import java.util.UUID;

import dev.zerite.craftlib.protocol.connection.NettyConnection;
import gg.mineral.server.command.CommandExecutor;

public class Player implements CommandExecutor {
    String name;
    UUID uuid;
    NettyConnection connection;

    public Player(String name, NettyConnection connection) {
        this.name = name;
        this.connection = connection;
    }

    public String getName() {
        return name;
    }

    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return uuid;
    }

    public NettyConnection getConnection() {
        return connection;
    }
}
