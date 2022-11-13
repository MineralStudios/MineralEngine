package gg.mineral.server.entity;

import gg.mineral.server.inventory.item.ItemStack;
import gg.mineral.server.util.math.EulerAngle;
import gg.mineral.server.util.math.Vector;

/**
 * The types of values that entity metadata can contain.
 */
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

    EntityMetadataType(Class<?> dataType) {
        this.dataType = dataType;
    }

    public static EntityMetadataType byId(int id) {
        return values()[id];
    }

    public Class<?> getDataType() {
        return dataType;
    }

    public int getId() {
        return ordinal();
    }
}