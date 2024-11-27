package gg.mineral.api.network.packet.rw;

import java.nio.charset.Charset;

public interface ByteRW {
    public static final Charset UTF_8 = Charset.forName("UTF-8");

    default int getVarIntSize(int value) {
        for (int position = 1; position < 5; ++position)
            if ((value & -1 << position * 7) == 0)
                return position;

        return 5;
    }
}
