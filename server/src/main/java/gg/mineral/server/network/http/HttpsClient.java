package gg.mineral.server.network.http;

import javax.net.ssl.HttpsURLConnection;

import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;

/**
 * Handles all the https requests including the pooling and re-usage of
 * connections.
 */
@RequiredArgsConstructor
public class HttpsClient {
    public enum Mode {
        POST, GET
    }

    private final String url;
    private final Mode mode;
    private String authorization, acceptLanguage;
    private MimeType contentType, accept;
    private boolean keepAlive;

    /**
     * Executes the https call. Use this method only for GET calls (GET calls don't
     * have a body).
     * 
     * @return The server's response.
     * @throws IOException If any IOException occurs while connecting the server.
     */
    public String send() throws IOException {
        return send(null);
    }

    /**
     * Executes the https call. Use this method only for POST calls (POST calls need
     * a body).
     * 
     * @param body The body as post input.
     * @return The server's response.
     * @throws IOException If any IOException occurs while connecting the server.
     */
    public String send(String body) throws IOException {
        if (body == null && mode == Mode.POST)
            throw new IllegalArgumentException("body can't be null for POST calls.");
        else if (body != null && mode == Mode.GET)
            throw new IllegalArgumentException("body must be null for GET calls.");

        val conn = getConnection();
        if (mode == Mode.POST && body != null) {
            val data = body.getBytes("UTF-8");
            conn.setRequestProperty("Content-Length", String.valueOf(data.length));
            conn.setDoOutput(true);
            try (val os = conn.getOutputStream()) {
                os.write(data);
            }
        }
        if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED &&
                conn.getResponseCode() != HttpsURLConnection.HTTP_OK)
            throw new RuntimeException("Failed : HTTP error code : "
                    + conn.getResponseCode());

        val out = new StringWriter();
        try (val is = new InputStreamReader(conn.getInputStream())) {
            copy(is, out);
            return out.toString();
        }
    }

    public HttpsClient setAcceptLanguage(String acceptLanguage) {
        this.acceptLanguage = acceptLanguage;
        return this;
    }

    public HttpsClient setContentType(MimeType contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpsClient setAccept(MimeType accept) {
        this.accept = accept;
        return this;
    }

    public HttpsClient setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    private HttpURLConnection getConnection() throws IOException {
        val turl = URI.create(url).toURL();
        val conn = (HttpsURLConnection) turl.openConnection();
        conn.setRequestMethod(mode == Mode.GET ? "GET" : "POST");
        conn.setDefaultUseCaches(false);
        if (this.keepAlive)
            conn.setRequestProperty("keep-alive", "true");

        if (authorization != null)
            conn.setRequestProperty("Authorization", authorization);

        if (acceptLanguage != null)
            conn.setRequestProperty("Accept-Language", acceptLanguage);

        if (contentType != null)
            conn.setRequestProperty("Content-Type", contentType.asString());

        if (accept != null)
            conn.setRequestProperty("Accept", accept.asString());

        return conn;
    }

    private static final int BUFFER_SIZE = 4 * 1024;

    private void copy(final Reader input, final Writer output) throws IOException {
        val buffer = new char[BUFFER_SIZE];
        for (int n = input.read(buffer); n != -1; n = input.read(buffer))
            output.write(buffer, 0, n);
    }
}
