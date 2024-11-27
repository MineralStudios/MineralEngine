package gg.mineral.server.network.ping;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONObject;

import com.eatthepath.uuid.FastUUID;

import gg.mineral.server.entity.living.human.PlayerImpl;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import kotlin.text.Charsets;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
// TODO: move to api
public final class ServerPing {
    private final String motd;
    private final int onlinePlayers, maxPlayers, protocol;
    private final String name;
    private final Icon icon;
    private final Map<UUID, String> playerSampleMap = new Object2ObjectOpenHashMap<>();

    public ServerPing(String motd, int onlinePlayers, int maxPlayers, int protocol, String name) {
        this(motd, onlinePlayers, maxPlayers, protocol, name, null);
    }

    public void addPlayerSample(PlayerImpl player) {
        playerSampleMap.put(player.getUuid(), player.getName());
    }

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
            val playerSample = new JSONArray();

            for (val e : playerSampleMap.entrySet())
                playerSample.put(new JSONObject()
                        .put("id", FastUUID.toString(e.getKey()))
                        .put("name", e.getValue()));

            players.put("sample", playerSample);
        }

        jsonObj.put("description", motd);
        jsonObj.put("players", players);
        jsonObj.put("version", version);

        if (icon != null && icon.hasData())
            jsonObj.put("favicon", icon.getData());

        return jsonObj.toString();
    }

    @Getter
    public static final class Icon {
        private String data = "";

        public Icon(String pathName) {
            val file = new File("server-icon.png");

            if (file.isFile()) {

                val buf = Unpooled.buffer();
                ByteBuf imageBuf = null;

                try {
                    val bufferedimage = ImageIO.read(file);

                    if (bufferedimage.getWidth() == 64 || bufferedimage.getHeight() == 64) {
                        ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(buf));
                        imageBuf = Base64.encode(buf);

                        this.data = "data:image/png;base64," + imageBuf.toString(Charsets.UTF_8);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (buf != null && buf.refCnt() > 0)
                        buf.release();

                    if (imageBuf != null)
                        imageBuf.release();
                }
            }
        }

        public boolean hasData() {
            return !data.isEmpty();
        }
    }
}
