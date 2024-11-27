package gg.mineral.api.entity.meta;

import java.util.Vector;

import gg.mineral.api.inventory.item.ItemStack;
import gg.mineral.api.math.EulerAngle;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The types of values that entity metadata can contain.
 */
@RequiredArgsConstructor
@Getter
public enum EntityMetadataType {
    BYTE(Byte.class),
    SHORT(Short.class),
    INT(Integer.class),
    FLOAT(Float.class),
    STRING(String.class),
    ITEM(ItemStack.class),
    VECTOR(Vector.class),
    EULER_ANGLE(EulerAngle.class);

    private final Class<?> dataType;

    public static EntityMetadataType byId(int id) {
        return values()[id];
    }

    public byte getId() {
        return (byte) ordinal();
    }
}