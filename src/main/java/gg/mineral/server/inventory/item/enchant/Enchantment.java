package gg.mineral.server.inventory.item.enchant;

import java.util.Map;

import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;

public class Enchantment {
    short id;
    static Map<Short, Enchantment> ENCHANTMENTS = new Short2ObjectOpenHashMap<>();

    static {
        // TODO register enchants
    }

    public Enchantment(short id) {
        this.id = id;
    }

    public short getId() {
        return id;
    }

    public static Enchantment getById(short id) {
        return ENCHANTMENTS.get(id);
    }
}
