package gg.mineral.server.util.collection;

import java.util.concurrent.atomic.AtomicReferenceArray;

import gg.mineral.server.util.math.MathUtil;

public class ShortNonBlockingArrayMap<T> {
    private final AtomicReferenceArray<T> array;

    public ShortNonBlockingArrayMap() {
        array = new AtomicReferenceArray<>(65535);
    }

    public T get(short index) {
        return array.get(MathUtil.unsigned(index));
    }

    public void set(short index, T value) {
        array.set(MathUtil.unsigned(index), value);
    }

    public int length() {
        return array.length();
    }

    public T computeIfAbsent(short key, T object) {
        return array.updateAndGet(MathUtil.unsigned(key), v -> v == null ? object : v);
    }
}
