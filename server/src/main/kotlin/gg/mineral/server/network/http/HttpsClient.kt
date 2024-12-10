package gg.mineral.server.network.http

import java.io.*
import java.net.HttpURLConnection
import java.net.URI
import javax.net.ssl.HttpsURLConnection

/**
 * Handles all the https requests including the pooling and re-usage of
 * connections.
 */
class HttpsClient(private val url: String, private val mode: Mode) {
    enum class Mode {
        POST, GET
    }

    private val authorization: String? = null
    private var acceptLanguage: String? = null
    private var contentType: MimeType? = null
    private var accept: MimeType? = null
    private var keepAlive = false

    /**
     * Executes the https call. Use this method only for POST calls (POST calls need
     * a body).
     *
     * @param body The body as post input.
     * @return The server's response.
     * @throws IOException If any IOException occurs while connecting the server.
     */
    /**
     * Executes the https call. Use this method only for GET calls (GET calls don't
     * have a body).
     *
     * @return The server's response.
     * @throws IOException If any IOException occurs while connecting the server.
     */
    @JvmOverloads
    @Throws(IOException::class)
    fun send(body: String? = null): String {
        require(!(body == null && mode == Mode.POST)) { "body can't be null for POST calls." }
        require(!(body != null && mode == Mode.GET)) { "body must be null for GET calls." }

        val conn = connection
        if (mode == Mode.POST && body != null) {
            val data = body.toByteArray(charset("UTF-8"))
            conn.setRequestProperty("Content-Length", data.size.toString())
            conn.doOutput = true
            conn.outputStream.use { os ->
                os.write(data)
            }
        }
        if (conn.responseCode != HttpURLConnection.HTTP_CREATED &&
            conn.responseCode != HttpsURLConnection.HTTP_OK
        ) throw RuntimeException(
            "Failed : HTTP error code : "
                    + conn.responseCode
        )

        val out = StringWriter()
        InputStreamReader(conn.inputStream).use { `is` ->
            copy(`is`, out)
            return out.toString()
        }
    }

    fun setAcceptLanguage(acceptLanguage: String?): HttpsClient {
        this.acceptLanguage = acceptLanguage
        return this
    }

    fun setContentType(contentType: MimeType?): HttpsClient {
        this.contentType = contentType
        return this
    }

    fun setAccept(accept: MimeType?): HttpsClient {
        this.accept = accept
        return this
    }

    fun setKeepAlive(keepAlive: Boolean): HttpsClient {
        this.keepAlive = keepAlive
        return this
    }

    @get:Throws(IOException::class)
    private val connection: HttpURLConnection
        get() {
            val turl = URI.create(url).toURL()
            val conn = turl.openConnection() as HttpsURLConnection
            conn.requestMethod = if (mode == Mode.GET) "GET" else "POST"
            conn.defaultUseCaches = false
            if (this.keepAlive) conn.setRequestProperty("keep-alive", "true")

            if (authorization != null) conn.setRequestProperty("Authorization", authorization)

            if (acceptLanguage != null) conn.setRequestProperty("Accept-Language", acceptLanguage)

            if (contentType != null) conn.setRequestProperty("Content-Type", contentType!!.asString())

            if (accept != null) conn.setRequestProperty("Accept", accept!!.asString())

            return conn
        }

    @Throws(IOException::class)
    private fun copy(input: Reader, output: Writer) {
        val buffer = CharArray(BUFFER_SIZE)
        var n = input.read(buffer)
        while (n != -1) {
            output.write(buffer, 0, n)
            n = input.read(buffer)
        }
    }

    companion object {
        private const val BUFFER_SIZE = 4 * 1024
    }
}
