package gg.mineral.server.util;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;

import gg.mineral.server.util.http.HttpsClient;
import gg.mineral.server.util.http.MimeType;

public class JsonUtil {
    public static JSONObject getJsonObject(String url) throws MalformedURLException, IOException {
        String response = doGetCall(url);
        return response == null ? null : new JSONObject(response);
    }

    private static String doGetCall(String url)
            throws IOException, MalformedURLException {
        return new HttpsClient(url, HttpsClient.Mode.GET).setAcceptLanguage("en_US")
                .setAccept(MimeType.JSON).send();
    }
}
