package gg.mineral.api.inventory.item.enchant

import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap
import lombok.RequiredArgsConstructor
import lombok.Value

@Value
@RequiredArgsConstructor
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
        fun getById(id: Short): Enchantment {
            return ENCHANTMENTS[id]
        }
    }
}
