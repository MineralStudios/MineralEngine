package gg.mineral.server.util.collection;

import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.Function;
import gg.mineral.server.util.math.MathUtil;

public class ShortNonBlockingArrayMap<T> {
    private static final int ARRAY_SIZE = 65536; // 0 to 65535
    private final AtomicReferenceArray<T> array;

    public ShortNonBlockingArrayMap() {
        array = new AtomicReferenceArray<>(ARRAY_SIZE);
    }

    /**
     * Returns the value at the given index.
     *
     * @param index the index
     * @return the value at the index
     */
    public T get(short index) {
        return array.get(MathUtil.unsigned(index));
    }

    /**
     * Sets the value at the given index.
     *
     * @param index the index
     * @param value the value to set
     */
    public void set(short index, T value) {
        array.set(MathUtil.unsigned(index), value);
    }

    /**
     * Returns the length of the array.
     *
     * @return the length of the array
     */
    public int length() {
        return array.length();
    }

    /**
     * Computes the value if absent using the given mapping function.
     *
     * @param index           the index
     * @param mappingFunction the function to compute the value
     * @return the current or newly computed value
     */
    public T computeIfAbsent(short index, Function<Short, T> mappingFunction) {
        int unsignedIndex = MathUtil.unsigned(index);
        T currentValue = array.get(unsignedIndex);
        if (currentValue == null) {
            T newValue = mappingFunction.apply(index);
            if (newValue == null) {
                throw new NullPointerException("Mapping function produced null value");
            }
            if (array.compareAndSet(unsignedIndex, null, newValue)) {
                return newValue;
            } else {
                // Another thread set a value before this one
                return array.get(unsignedIndex);
            }
        } else {
            return currentValue;
        }
    }
}
