package gg.mineral.server.network.ping;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.eatthepath.uuid.FastUUID;

import gg.mineral.server.util.icon.IconUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public class ServerPing {

    String motd;
    int maxPlayers, onlinePlayers, protocol;
    String name;
    Map<UUID, String> PLAYER_SAMPLE = new Object2ObjectOpenHashMap<>();

    public ServerPing(String motd, int onlinePlayers, int maxPlayers, int protocol, String name) {
        this.motd = motd;
        this.onlinePlayers = onlinePlayers;
        this.maxPlayers = maxPlayers;
        this.protocol = protocol;
        this.name = name;
    }

    public void addPlayerSample(UUID uuid, String name) {
        PLAYER_SAMPLE.put(uuid, name);
    }

    public void addLineToSample(String string) {
        PLAYER_SAMPLE.put(UUID.randomUUID(), string);
    }

    public String toJsonString() {
        JSONObject jsonObj = new JSONObject();

        JSONObject version = new JSONObject()
                .put("name", name)
                .put("protocol", protocol);

        JSONObject players = new JSONObject()
                .put("max", maxPlayers)
                .put("online", onlinePlayers);

        if (!PLAYER_SAMPLE.isEmpty()) {
            JSONArray playerSample = new JSONArray();

            for (Entry<UUID, String> e : PLAYER_SAMPLE.entrySet()) {
                playerSample.put(new JSONObject()
                        .put("id", FastUUID.toString(e.getKey()))
                        .put("name", e.getValue()));
            }

            players.put("sample", playerSample);
        }

        jsonObj.put("description", motd);
        jsonObj.put("players", players);
        jsonObj.put("version", version);

        if (IconUtil.ICON != null)
            jsonObj.put("favicon", IconUtil.ICON);

        return jsonObj.toString();
    }
}
