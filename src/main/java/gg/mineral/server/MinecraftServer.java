package gg.mineral.server;

import java.util.concurrent.ExecutionException;

import dev.zerite.craftlib.protocol.version.MinecraftProtocol;
import kotlin.Unit;

public class MinecraftServer {
    public static void main(String[] args) {
        try {
            MinecraftProtocol.listenFuture(25565, config -> {
                config.debug = true;
                config.noDelay = true;
                config.packetHandler = new ServerConnection();
                return Unit.INSTANCE;
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
