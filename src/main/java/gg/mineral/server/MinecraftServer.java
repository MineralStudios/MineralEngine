package gg.mineral.server;

import dev.zerite.craftlib.protocol.version.MinecraftProtocol;
import kotlin.Unit;

public class MinecraftServer {
    public static void main(String[] args) {
        MinecraftProtocol.listenFuture(25565, config -> {
            config.debug = true;
            config.noDelay = true;
            config.packetHandler = new ServerConnection();
            return Unit.INSTANCE;
        }).join();
    }
}
