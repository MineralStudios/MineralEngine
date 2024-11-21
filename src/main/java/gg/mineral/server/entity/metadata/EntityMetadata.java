package gg.mineral.server.entity.metadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import gg.mineral.server.entity.Entity;
import gg.mineral.server.inventory.item.ItemStack;
import gg.mineral.server.util.collection.GlueList;
import lombok.RequiredArgsConstructor;
import lombok.val;

/**
 * A map for entity metadata.
 */
public class EntityMetadata {

    private final Map<EntityMetadataIndex, Object> map = new EnumMap<>(EntityMetadataIndex.class);
    private final List<Entry> changes = new ArrayList<>(4);
    private final Class<? extends Entity> entityClass;

    public EntityMetadata(Class<? extends Entity> entityClass) {
        this.entityClass = entityClass;
        set(EntityMetadataIndex.STATUS, 0); // all entities have to have at least this
    }

    public boolean containsKey(EntityMetadataIndex index) {
        return map.containsKey(index);
    }

    public void set(EntityMetadataIndex index, Object value) {
        // take numbers down to the correct precision
        if (value instanceof Number) {
            Number n = (Number) value;
            switch (index.getType()) {
                case BYTE:
                    value = n.byteValue();
                    break;
                case SHORT:
                    value = n.shortValue();
                    break;
                case INT:
                    value = n.intValue();
                    break;
                case FLOAT:
                    value = n.floatValue();
                    break;
                default:
                    break;
            }
        }

        if (!index.getType().getDataType().isAssignableFrom(value.getClass()))
            throw new IllegalArgumentException(
                    "Cannot assign " + value + " to " + index + ", expects " + index.getType());

        if (!index.appliesTo(entityClass))
            throw new IllegalArgumentException("Index " + index + " does not apply to " + entityClass.getSimpleName()
                    + ", only " + index.getAppliesTo().getSimpleName());

        val prev = map.put(index, value);
        if (!Objects.equals(prev, value))
            changes.add(new Entry(index, value));
    }

    public Object get(EntityMetadataIndex index) {
        return map.get(index);
    }

    public boolean getBit(EntityMetadataIndex index, int bit) {
        return (getNumber(index).intValue() & bit) != 0;
    }

    public void setBit(EntityMetadataIndex index, int bit, boolean status) {
        if (status)
            set(index, getNumber(index).intValue() | bit);
        else
            set(index, getNumber(index).intValue() & ~bit);
    }

    public Number getNumber(EntityMetadataIndex index) {
        if (!containsKey(index))
            return 0;

        val o = get(index);
        if (!(o instanceof Number))
            throw new IllegalArgumentException("Index " + index + " is of non-number type " + index.getType());

        return (Number) o;
    }

    public byte getByte(EntityMetadataIndex index) {
        return get(index, EntityMetadataType.BYTE, (byte) 0);
    }

    public short getShort(EntityMetadataIndex index) {
        return get(index, EntityMetadataType.SHORT, (short) 0);
    }

    public int getInt(EntityMetadataIndex index) {
        return get(index, EntityMetadataType.INT, 0);
    }

    public float getFloat(EntityMetadataIndex index) {
        return get(index, EntityMetadataType.FLOAT, 0f);
    }

    public String getString(EntityMetadataIndex index) {
        return get(index, EntityMetadataType.STRING, null);
    }

    public ItemStack getItem(EntityMetadataIndex index) {
        return get(index, EntityMetadataType.ITEM, null);
    }

    @SuppressWarnings("unchecked")
    private <T> T get(EntityMetadataIndex index, EntityMetadataType expected, T def) {
        if (index.getType() != expected)
            throw new IllegalArgumentException("Cannot get " + index + ": is " + index.getType() + ", not " + expected);

        val t = (T) map.get(index);
        return t == null ? def : t;
    }

    public List<Entry> getEntryList() {
        val result = new ArrayList<Entry>(map.size());
        result.addAll(map.entrySet().stream().map(entry -> new Entry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList()));
        Collections.sort(result);
        return result;
    }

    public List<Entry> getChanges() {
        Collections.sort(changes);
        return new GlueList<>(changes);
    }

    public void resetChanges() {
        changes.clear();
    }

    @RequiredArgsConstructor
    public static final class Entry implements Comparable<Entry> {
        public final EntityMetadataIndex index;
        public final Object value;

        @Override
        public int compareTo(Entry o) {
            return o.index.getIndex() - index.getIndex();
        }

        @Override
        public String toString() {
            return index + "=" + value;
        }
    }
}