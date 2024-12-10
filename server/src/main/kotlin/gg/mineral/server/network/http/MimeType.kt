package gg.mineral.server.network.http

enum class MimeType(private val mimeType: String) {
    JSON("application/json");

    fun asString(): String =
        mimeType
}
