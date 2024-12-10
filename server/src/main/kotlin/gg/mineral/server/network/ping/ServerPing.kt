package gg.mineral.server.network.ping

import com.eatthepath.uuid.FastUUID
import gg.mineral.server.entity.living.human.PlayerImpl
import io.netty.buffer.ByteBuf
import io.netty.buffer.ByteBufOutputStream
import io.netty.buffer.Unpooled
import io.netty.handler.codec.base64.Base64
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.*
import javax.imageio.ImageIO

class ServerPing(
    val motd: String,
    val onlinePlayers: Int,
    val maxPlayers: Int,
    val protocol: Int,
    val name: String,
    val icon: Icon? = null
) {
    private val playerSampleMap: MutableMap<UUID?, String> = Object2ObjectOpenHashMap()

    fun addPlayerSample(player: PlayerImpl) {
        playerSampleMap[player.uuid] = player.name
    }

    fun addPlayerSample(uuid: UUID?, name: String) {
        playerSampleMap[uuid] = name
    }

    fun addLineToSample(string: String) {
        playerSampleMap[UUID.randomUUID()] = string
    }

    fun toJsonString(): String {
        val jsonObj = JSONObject()

        val version = JSONObject()
            .put("name", name)
            .put("protocol", protocol)

        val players = JSONObject()
            .put("max", maxPlayers)
            .put("online", onlinePlayers)

        if (!playerSampleMap.isEmpty()) {
            val playerSample = JSONArray()

            for ((key, value) in playerSampleMap) playerSample.put(
                JSONObject()
                    .put("id", FastUUID.toString(key))
                    .put("name", value)
            )

            players.put("sample", playerSample)
        }

        jsonObj.put("description", motd)
        jsonObj.put("players", players)
        jsonObj.put("version", version)

        if (icon != null && icon.hasData()) jsonObj.put("favicon", icon.data)

        return jsonObj.toString()
    }

    class Icon(pathName: String) {
        var data = ""

        init {
            val file = File("server-icon.png")

            if (file.isFile) {
                val buf = Unpooled.buffer()
                var imageBuf: ByteBuf? = null

                try {
                    val bufferedimage = ImageIO.read(file)

                    if (bufferedimage.width == 64 || bufferedimage.height == 64) {
                        ImageIO.write(bufferedimage, "PNG", ByteBufOutputStream(buf))
                        imageBuf = Base64.encode(buf)

                        this.data = "data:image/png;base64," + imageBuf.toString(StandardCharsets.UTF_8)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } finally {
                    if (buf != null && buf.refCnt() > 0) buf.release()

                    imageBuf?.release()
                }
            }
        }

        fun hasData(): Boolean = data.isNotEmpty()
    }
}
