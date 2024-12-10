package gg.mineral.api.entity.meta

import lombok.RequiredArgsConstructor

interface EntityMetadata {
    @RequiredArgsConstructor
    class Entry : Comparable<Entry> {
        val index: EntityMetadataIndex? = null
        val value: Any? = null

        override fun compareTo(o: Entry): Int {
            return o.index!!.index - index!!.index
        }

        override fun toString(): String {
            return index.toString() + "=" + value
        }
    }
}
