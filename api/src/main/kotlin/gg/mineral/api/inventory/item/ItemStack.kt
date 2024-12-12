package gg.mineral.api.inventory.item

import gg.mineral.api.inventory.item.enchant.Enchantment
import gg.mineral.api.nbt.CompoundTag
import gg.mineral.api.nbt.TagType
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import java.util.*

open class ItemStack(
    val typeId: Short,
    var amount: UByte = 1.toUByte(),
    var durability: Short = 1
) {
    private var displayName: String = ""
    private var lore: List<String> = Collections.emptyList()
    private var enchants: Object2IntOpenHashMap<Enchantment> = Object2IntOpenHashMap()
    private var hideFlag = 0

    fun hasDisplayName(): Boolean = displayName.isNotEmpty()

    fun hasLore(): Boolean = lore.isNotEmpty()

    fun hasEnchants(): Boolean {
        return !enchants.isEmpty()
    }

    fun writeNbt(tag: CompoundTag) {
        val displayTags = CompoundTag()
        if (hasDisplayName()) displayTags.putString("Name", displayName)

        if (hasLore()) displayTags.putList("Lore", TagType.STRING, lore)

        if (!displayTags.isEmpty) tag.putCompound("display", displayTags)

        if (hasEnchants()) writeNbtEnchants("ench", tag, enchants)

        if (hideFlag != 0) tag.putInt("HideFlags", hideFlag)
    }

    fun readNbt(tag: CompoundTag?) {
        if (tag == null) return
        if (tag.isCompound("display")) {
            val display = tag.getCompound("display")
            if (display.isString("Name")) displayName = display.getString("Name") ?: ""

            if (display.isList("Lore", TagType.STRING)) lore = display.getList("Lore", TagType.STRING)
        }

        val tagEnchants = readNbtEnchants("ench", tag)
        if (tagEnchants != null)
            enchants.putAll(tagEnchants)

        if (tag.isInt("HideFlags")) hideFlag = tag.getInt("HideFlags")
    }

    companion object {
        protected fun writeNbtEnchants(
            name: String, to: CompoundTag,
            enchants: Object2IntOpenHashMap<Enchantment>
        ) {
            val ench = ArrayList<CompoundTag>()

            for (enchantment in enchants.object2IntEntrySet()) {
                val enchantmentTag = CompoundTag()
                enchantmentTag.putShort("id", enchantment.key.id.toInt())
                enchantmentTag.putShort("lvl", enchantment.intValue)
                ench.add(enchantmentTag)
            }

            to.putCompoundList(name, ench)
        }

        protected fun readNbtEnchants(name: String, tag: CompoundTag): Object2IntOpenHashMap<Enchantment>? {
            var result: Object2IntOpenHashMap<Enchantment>? = null

            if (tag.isList(name, TagType.COMPOUND)) {
                val enchs = tag.getCompoundList(name)
                for (enchantmentTag in enchs) {
                    if (enchantmentTag.isShort("id") && enchantmentTag.isShort("lvl")) {
                        val enchantment = Enchantment.getById(enchantmentTag.getShort("id"))
                        if (result == null) result = Object2IntOpenHashMap(4)
                        result.put(enchantment, enchantmentTag.getShort("lvl").toInt())
                    }
                }
            }

            return result
        }
    }
}
