package gg.mineral.api.inventory.item

import gg.mineral.api.inventory.item.enchant.Enchantment
import gg.mineral.api.nbt.CompoundTag
import gg.mineral.api.nbt.TagType
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import lombok.Getter
import lombok.RequiredArgsConstructor
import lombok.Setter

@RequiredArgsConstructor
class ItemStack(
    @field:Getter private val typeId: Short,
    @field:Getter private var amount: Short,
    @field:Getter private var durability: Short
) {
    @Getter
    @Setter
    private val displayName: String? = null

    @Getter
    @Setter
    private val lore: List<String>? = null
    private var enchants: Object2IntOpenHashMap<Enchantment>? = null
    private var hideFlag = 0

    fun hasDisplayName(): Boolean {
        return displayName != null && !displayName.isEmpty()
    }

    fun hasLore(): Boolean {
        return lore != null && !lore.isEmpty()
    }

    fun hasEnchants(): Boolean {
        return enchants != null && !enchants!!.isEmpty()
    }

    fun writeNbt(tag: CompoundTag) {
        val displayTags = CompoundTag()
        if (hasDisplayName()) displayTags.putString("Name", getDisplayName())

        if (hasLore()) displayTags.putList<String>("Lore", TagType.STRING, getLore())

        if (!displayTags.isEmpty) tag.putCompound("display", displayTags)

        if (hasEnchants()) writeNbtEnchants("ench", tag, enchants!!)

        if (hideFlag != 0) tag.putInt("HideFlags", hideFlag)
    }

    fun readNbt(tag: CompoundTag?) {
        if (tag == null) return
        if (tag.isCompound("display")) {
            val display = tag.getCompound("display")
            if (display.isString("Name")) setDisplayName(display.getString("Name"))

            if (display.isList("Lore", TagType.STRING)) setLore(display.getList<String>("Lore", TagType.STRING))
        }

        val tagEnchants = readNbtEnchants("ench", tag)
        if (tagEnchants != null) {
            if (enchants == null) enchants = tagEnchants
            else enchants!!.putAll(tagEnchants)
        }

        if (tag.isInt("HideFlags")) hideFlag = tag.getInt("HideFlags")
    }

    companion object {
        protected fun writeNbtEnchants(
            name: String?, to: CompoundTag,
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

        protected fun readNbtEnchants(name: String?, tag: CompoundTag): Object2IntOpenHashMap<Enchantment>? {
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
