package gg.mineral.api.entity.meta;

import lombok.RequiredArgsConstructor;

public interface EntityMetadata {

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
