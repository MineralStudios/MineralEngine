package gg.mineral.server.network.http;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum MimeType {
    JSON("application/json");

    private final String str;

    public String asString() {
        return str;
    }
}
