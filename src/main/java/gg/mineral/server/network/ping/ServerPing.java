package gg.mineral.server.network.ping;

import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.eatthepath.uuid.FastUUID;

import gg.mineral.server.util.icon.IconUtil;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class ServerPing {
    private final String motd;
    private final int onlinePlayers, maxPlayers, protocol;
    private final String name;
    private final Map<UUID, String> playerSampleMap = new Object2ObjectOpenHashMap<>();

    public void addPlayerSample(UUID uuid, String name) {
        playerSampleMap.put(uuid, name);
    }

    public void addLineToSample(String string) {
        playerSampleMap.put(UUID.randomUUID(), string);
    }

    public String toJsonString() {
        val jsonObj = new JSONObject();

        val version = new JSONObject()
                .put("name", name)
                .put("protocol", protocol);

        val players = new JSONObject()
                .put("max", maxPlayers)
                .put("online", onlinePlayers);

        if (!playerSampleMap.isEmpty()) {
            JSONArray playerSample = new JSONArray();

            for (val e : playerSampleMap.entrySet())
                playerSample.put(new JSONObject()
                        .put("id", FastUUID.toString(e.getKey()))
                        .put("name", e.getValue()));

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
