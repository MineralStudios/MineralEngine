package gg.mineral.server.util.json;

import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONObject;

import gg.mineral.server.network.http.HttpsClient;
import gg.mineral.server.network.http.MimeType;

public class JsonUtil {

    public static JSONObject getJsonObject(String url) {
        String response;
        try {
            response = doGetCall(url);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return response == null ? null : new JSONObject(response);
    }

    private static String doGetCall(String url)
            throws IOException, MalformedURLException {
        return new HttpsClient(url, HttpsClient.Mode.GET).setAcceptLanguage("en_US")
                .setAccept(MimeType.JSON).send();
    }
}
