package gg.mineral.server.util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;

import gg.mineral.server.util.http.HttpsClient;
import gg.mineral.server.util.http.MimeType;

public class JsonUtil {
    public static CompletableFuture<JSONObject> getJsonObject(String url) {
        return CompletableFuture.supplyAsync(() -> {
            String response;
            try {
                response = doGetCall(url);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
            return response == null ? null : new JSONObject(response);
        });
    }

    private static String doGetCall(String url)
            throws IOException, MalformedURLException {
        return new HttpsClient(url, HttpsClient.Mode.GET).setAcceptLanguage("en_US")
                .setAccept(MimeType.JSON).send();
    }
}
