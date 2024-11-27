package gg.mineral.api.inventory.item.enchant;

import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Enchantment {
    short id;
    private final static Short2ObjectOpenHashMap<Enchantment> ENCHANTMENTS = new Short2ObjectOpenHashMap<>();
    // TODO register enchants
    /*
     * static {
     * 
     * }
     */

    public static Enchantment getById(short id) {
        return ENCHANTMENTS.get(id);
    }
}
