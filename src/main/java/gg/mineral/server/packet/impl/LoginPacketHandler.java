package gg.mineral.server.packet.impl;

import java.io.IOException;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.json.JSONObject;

import dev.zerite.craftlib.protocol.Packet;
import dev.zerite.craftlib.protocol.connection.NettyConnection;
import dev.zerite.craftlib.protocol.packet.login.client.ClientLoginEncryptionResponsePacket;
import dev.zerite.craftlib.protocol.packet.login.client.ClientLoginStartPacket;
import dev.zerite.craftlib.protocol.packet.login.server.ServerLoginEncryptionRequestPacket;
import dev.zerite.craftlib.protocol.packet.login.server.ServerLoginSuccessPacket;
import gg.mineral.server.entity.Player;
import gg.mineral.server.entity.PlayerManager;
import gg.mineral.server.packet.ILoginPacketHandler;
import gg.mineral.server.util.EncryptionUtil;
import gg.mineral.server.util.JsonUtil;
import gg.mineral.server.util.UUIDUtil;

public class LoginPacketHandler implements ILoginPacketHandler {

    KeyPair KEY_PAIR;
    byte[] VERIFY_TOKEN = new byte[4];
    SecretKey SHARED_SECRET;

    @Override
    public void handle(NettyConnection connection, Packet packet) {
        if (packet instanceof ClientLoginStartPacket) {
            handle(connection, (ClientLoginStartPacket) packet);
        } else if (packet instanceof ClientLoginEncryptionResponsePacket) {
            handle(connection, (ClientLoginEncryptionResponsePacket) packet);
        }

    }

    @Override
    public void handle(NettyConnection connection, ClientLoginStartPacket packet) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            KEY_PAIR = keyPairGenerator.genKeyPair();
            new Random().nextBytes(VERIFY_TOKEN);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        PlayerManager.create(packet.getName(), connection);
        connection.send(new ServerLoginEncryptionRequestPacket("", KEY_PAIR.getPublic(), VERIFY_TOKEN));
    }

    @Override
    public void handle(NettyConnection connection, ClientLoginEncryptionResponsePacket packet) {
        if (!Arrays.equals(VERIFY_TOKEN, packet.getVerifyToken(KEY_PAIR.getPrivate()))) {
            throw new IllegalStateException("Invalid nonce!");
        }

        SHARED_SECRET = packet.getSecretKey(KEY_PAIR.getPrivate());
        String serverId = (new BigInteger(EncryptionUtil.encrypt("",
                KEY_PAIR.getPublic(), SHARED_SECRET))).toString(16);

        Player player = PlayerManager.get(p -> p.getConnection().equals(connection));

        String url = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=" + player.getName()
                + "&serverId="
                + serverId;

        try {
            JSONObject json = JsonUtil.getJsonObject(url);

            if (json == null) {
                PlayerManager.remove(p -> p.getConnection().equals(connection));
                // disconnect
                return;
            }

            String name = json.getString("name");

            if (!player.getName().equalsIgnoreCase(name)) {
                PlayerManager.remove(p -> p.getConnection().equals(connection));
                // disconnect
                return;
            }

            UUID uuid = UUIDUtil.fromString(json.getString("id"));

            player.setUUID(uuid);

            connection.send(new ServerLoginSuccessPacket(uuid, name));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
