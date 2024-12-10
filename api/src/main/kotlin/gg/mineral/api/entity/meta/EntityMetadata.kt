package gg.mineral.api.entity.meta

interface EntityMetadata {
    class Entry(val index: EntityMetadataIndex, val value: Any) : Comparable<Entry> {
        override fun compareTo(other: Entry): Int = other.index.index - index.index

        override fun toString(): String = "$index=$value"
    }
}
