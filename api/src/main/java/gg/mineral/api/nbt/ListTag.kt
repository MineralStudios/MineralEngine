package gg.mineral.api.nbt

import gg.mineral.api.nbt.Tag.type

/**
 * The `TAG_List` tag.
 */
internal class ListTag<T : Tag<*>?>(
    /**
     * The type of entries within this list.
     */
    val childType: TagType, value: List<T>
) :
    Tag<List<T>?>(TagType.LIST) {
    /**
     * Gets the type of item in this list.
     *
     * @return The type of item in this list.
     */

    /**
     * The value.
     */
    override val value: List<T> =
        ArrayList(value) // modifying list should not modify tag

    /**
     * Creates the tag.
     *
     * @param type  The type of item in the list.
     * @param value The value.
     */
    init {
        // ensure type of objects in list matches tag type
        for (elem in value) {
            require(!(elem != null && childType !== elem.type)) { "ListTag(" + childType + ") cannot hold tags of type " + elem!!.type }
        }
    }

    override fun valueToString(bldr: StringBuilder) {
        bldr.append(value.size).append(" entries of type ").append(childType.getName()).append("\n{\n")
        for (elem in value) {
            if (elem == null) {
                bldr.append("    null\n")
                continue
            }
            bldr.append("    ").append(elem.toString().replace("\n".toRegex(), "\n    ")).append("\n")
        }
        bldr.append("}")
    }
}
