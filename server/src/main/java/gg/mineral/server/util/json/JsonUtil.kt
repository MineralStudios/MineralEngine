package gg.mineral.server.util.json

import gg.mineral.server.network.http.HttpsClient
import gg.mineral.server.network.http.MimeType
import lombok.SneakyThrows
import org.json.JSONObject
import java.io.IOException
import java.net.MalformedURLException

object JsonUtil {
    @SneakyThrows
    fun getJsonObject(url: String): JSONObject {
        val response = doGetCall(url)
        return JSONObject(response)
    }

    @Throws(IOException::class, MalformedURLException::class)
    private fun doGetCall(url: String): String {
        return HttpsClient(url, HttpsClient.Mode.GET).setAcceptLanguage("en_US")
            .setAccept(MimeType.JSON).send()
    }
}
