package gg.mineral.server.entity;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;

import dev.zerite.craftlib.chat.component.BaseChatComponent;
import gg.mineral.server.command.CommandExecutor;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.login.LoginAuthData;
import gg.mineral.server.network.packet.login.clientbound.DisconnectPacket;
import gg.mineral.server.network.packet.login.clientbound.EncryptionRequestPacket;
import gg.mineral.server.util.datatypes.UUIDUtil;
import gg.mineral.server.util.json.JsonUtil;
import gg.mineral.server.util.login.LoginUtil;

public class Player extends CommandExecutor {
    String name;
    UUID uuid;
    Connection connection;
    LoginAuthData loginAuthData;

    public Player(String name, Connection connection) {
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

    public Connection getConnection() {
        return connection;
    }

    public void login() {
        this.loginAuthData = new LoginAuthData();
        connection.sendPacket(new EncryptionRequestPacket("",
                this.loginAuthData.getKeyPair().getPublic(), this.loginAuthData.getVerifyToken()));
    }

    public CompletableFuture<Boolean> authenticate(byte[] encryptedSharedSecret, byte[] encryptedVerifyToken) {
        return CompletableFuture.supplyAsync(() -> {
            if (!Arrays.equals(this.loginAuthData.getVerifyToken(),
                    LoginUtil.decryptRsa(this.loginAuthData.getKeyPair(), encryptedVerifyToken))) {
                return false;
            }

            String serverId = LoginUtil.hashSharedSecret(this.loginAuthData.getKeyPair().getPublic(),
                    LoginUtil.decryptRsa(this.loginAuthData.getKeyPair(), encryptedSharedSecret));

            String url = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" +
                    this.getName()
                    + "&serverId="
                    + serverId;

            JSONObject json = JsonUtil.getJsonObject(url);

            if (json == null) {
                return false;
            }

            UUID uuid = UUIDUtil.fromString(json.getString("id"));

            this.setUUID(uuid);

            return true;
        });
    }

    public void disconnect(BaseChatComponent chatComponent) {
        PlayerManager.LIST.remove(this);
        connection.sendPacket(new DisconnectPacket(chatComponent));
        connection.close();
    }

}
