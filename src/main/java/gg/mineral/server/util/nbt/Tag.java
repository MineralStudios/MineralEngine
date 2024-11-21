package gg.mineral.server.util.nbt;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * Represents a single NBT tag.
 */
@RequiredArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Getter
public abstract class Tag<T> {
    /**
     * The type of this tag.
     */
    private final TagType type;

    /**
     * Gets the value of this tag.
     *
     * @return The value of this tag.
     */
    @NonNull
    public abstract T getValue();

    @Override
    public final String toString() {
        val builder = new StringBuilder("TAG_");
        builder.append(type.getName()).append(": ");
        valueToString(builder);
        return builder.toString();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        val tag = (Tag<?>) o;

        return type == tag.type && getValue().equals(tag.getValue());
    }

    @Override
    public final int hashCode() {
        int result = type.hashCode();
        result = 31 * result + getValue().hashCode();
        return result;
    }

    protected void valueToString(StringBuilder builder) {
        builder.append(getValue());
    }
}
