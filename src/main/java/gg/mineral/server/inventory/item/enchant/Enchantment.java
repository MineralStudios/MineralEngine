package gg.mineral.server.inventory.item.enchant;

import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class Enchantment {
    short id;
    static Short2ObjectOpenHashMap<Enchantment> ENCHANTMENTS = new Short2ObjectOpenHashMap<>();

    static {
        // TODO register enchants
    }

    public static Enchantment getById(short id) {
        return ENCHANTMENTS.get(id);
    }
}
