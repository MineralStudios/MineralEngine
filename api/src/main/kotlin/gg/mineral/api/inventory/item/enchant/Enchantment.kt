package gg.mineral.api.inventory.item.enchant

import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap

class Enchantment {
    var id: Short = 0

    companion object {
        private val ENCHANTMENTS = Short2ObjectOpenHashMap<Enchantment>()

        // TODO register enchants
        /*
     * static {
     * 
     * }
     */
        fun getById(id: Short): Enchantment =
            ENCHANTMENTS[id]
    }
}
