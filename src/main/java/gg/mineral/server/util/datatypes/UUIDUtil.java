package gg.mineral.server.util.datatypes;

import java.util.UUID;

import com.eatthepath.uuid.FastUUID;

public class UUIDUtil {
    public static String fromUUID(UUID uuid) {
        return FastUUID.toString(uuid).replace("-", "");
    }

    public static UUID fromString(String string) {
        return FastUUID.parseUUID(string.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
    }
}
