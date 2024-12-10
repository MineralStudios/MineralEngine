package gg.mineral.server.network.http

import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
enum class MimeType {
    JSON("application/json");

    private val str: String? = null

    fun asString(): String? {
        return str
    }
}
