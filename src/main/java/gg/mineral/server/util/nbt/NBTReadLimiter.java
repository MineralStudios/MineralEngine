package gg.mineral.server.util.nbt;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NBTReadLimiter {

    public static final NBTReadLimiter UNLIMITED = new NBTReadLimiter(0L) {

        @Override
        public void read(int length) {
        }
    };

    private final long limit;
    private long read = 0;

    public void read(int length) {
        this.read += length;
        if (this.read > this.limit)
            throw new IllegalStateException("Read more than " + this.limit + " bytes from NBT tag");
    }
}
