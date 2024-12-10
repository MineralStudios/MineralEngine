package gg.mineral.server.util.json

import gg.mineral.server.network.http.HttpsClient
import gg.mineral.server.network.http.MimeType
import org.json.JSONObject
import java.io.IOException
import java.net.MalformedURLException

object JsonUtil {
    fun getJsonObject(url: String): JSONObject =
        JSONObject(doGetCall(url))

    @Throws(IOException::class, MalformedURLException::class)
    private fun doGetCall(url: String): String = HttpsClient(url, HttpsClient.Mode.GET).setAcceptLanguage("en_US")
        .setAccept(MimeType.JSON).send()
}
