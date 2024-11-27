package gg.mineral.api.nbt;

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

        val value = getValue();

        return type == tag.type && value != null && value.equals(tag.getValue());
    }

    @Override
    public final int hashCode() {
        int result = type.hashCode();
        val value = getValue();
        if (value != null)
            result = 31 * result + value.hashCode();
        return result;
    }

    protected void valueToString(StringBuilder builder) {
        builder.append(getValue());
    }
}
