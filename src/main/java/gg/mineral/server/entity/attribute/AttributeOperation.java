package gg.mineral.server.entity.attribute;

import org.jetbrains.annotations.Nullable;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AttributeOperation {
    ADDITION(0),
    MULTIPLY_BASE(1),
    MULTIPLY_TOTAL(2);

    private static final AttributeOperation[] VALUES = new AttributeOperation[] { ADDITION, MULTIPLY_BASE,
            MULTIPLY_TOTAL };
    private final int id;

    @Nullable
    public static AttributeOperation fromId(int id) {
        if (id >= 0 && id < VALUES.length)
            return VALUES[id];

        return null;
    }
}