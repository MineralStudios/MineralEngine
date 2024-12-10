package gg.mineral.api.nbt

/**
 * The `TAG_List` tag.
 */
internal class ListTag<T : Tag<*>>(
    /**
     * The type of entries within this list.
     */
    val childType: TagType, value: List<T>
) :
    Tag<List<T>>(TagType.LIST) {
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
        for (elem in value)
            require(childType === elem.type) { "ListTag(" + childType + ") cannot hold tags of type " + elem.type }
    }

    override fun valueToString(builder: StringBuilder) {
        builder.append(value.size).append(" entries of type ").append(childType.name).append("\n{\n")
        for (elem in value)
            builder.append("    ").append(elem.toString().replace("\n".toRegex(), "\n    ")).append("\n")

        builder.append("}")
    }
}
