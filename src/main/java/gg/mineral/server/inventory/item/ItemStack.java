package gg.mineral.server.inventory.item;

import java.util.List;
import java.util.Map;

import gg.mineral.server.inventory.item.enchant.Enchantment;
import gg.mineral.server.util.collection.GlueList;
import gg.mineral.server.util.nbt.CompoundTag;
import gg.mineral.server.util.nbt.TagType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.Setter;

public class ItemStack {

    @Getter
    @Setter
    String displayName;
    @Getter
    @Setter
    List<String> lore;
    Map<Enchantment, Integer> enchants;
    int hideFlag;
    short amount, durability;
    Material material;

    public ItemStack(Material material, short amount, short durability) {
        this.material = material;
        this.amount = amount;
        this.durability = durability;
    }

    public boolean hasDisplayName() {
        return displayName != null && !displayName.isEmpty();
    }

    public boolean hasLore() {
        return lore != null && !lore.isEmpty();
    }

    public boolean hasEnchants() {
        return enchants != null && !enchants.isEmpty();
    }

    public void writeNbt(CompoundTag tag) {
        CompoundTag displayTags = new CompoundTag();
        if (hasDisplayName()) {
            displayTags.putString("Name", getDisplayName());
        }
        if (hasLore()) {
            displayTags.putList("Lore", TagType.STRING, getLore());
        }

        if (!displayTags.isEmpty()) {
            tag.putCompound("display", displayTags);
        }

        if (hasEnchants()) {
            writeNbtEnchants("ench", tag, enchants);
        }

        if (hideFlag != 0) {
            tag.putInt("HideFlags", hideFlag);
        }
    }

    public void readNbt(CompoundTag tag) {
        if (tag.isCompound("display")) {
            CompoundTag display = tag.getCompound("display");
            if (display.isString("Name")) {
                setDisplayName(display.getString("Name"));
            }
            if (display.isList("Lore", TagType.STRING)) {
                setLore(display.<String>getList("Lore", TagType.STRING));
            }
        }

        Map<Enchantment, Integer> tagEnchants = readNbtEnchants("ench", tag);
        if (tagEnchants != null) {
            if (enchants == null)
                enchants = tagEnchants;
            else
                enchants.putAll(tagEnchants);
        }

        if (tag.isInt("HideFlags")) {
            hideFlag = tag.getInt("HideFlags");
        }
    }

    protected static void writeNbtEnchants(String name, CompoundTag to, Map<Enchantment, Integer> enchants) {
        List<CompoundTag> ench = new GlueList<>();

        for (Map.Entry<Enchantment, Integer> enchantment : enchants.entrySet()) {
            CompoundTag enchantmentTag = new CompoundTag();
            enchantmentTag.putShort("id", enchantment.getKey().getId());
            enchantmentTag.putShort("lvl", enchantment.getValue());
            ench.add(enchantmentTag);
        }

        to.putCompoundList(name, ench);
    }

    protected static Map<Enchantment, Integer> readNbtEnchants(String name, CompoundTag tag) {
        Map<Enchantment, Integer> result = null;

        if (tag.isList(name, TagType.COMPOUND)) {
            Iterable<CompoundTag> enchs = tag.getCompoundList(name);
            for (CompoundTag enchantmentTag : enchs) {
                if (enchantmentTag.isShort("id") && enchantmentTag.isShort("lvl")) {
                    Enchantment enchantment = Enchantment.getById(enchantmentTag.getShort("id"));
                    if (result == null)
                        result = new Object2IntOpenHashMap<>(4);
                    result.put(enchantment, (int) enchantmentTag.getShort("lvl"));
                }
            }
        }

        return result;
    }

    public int getTypeId() {
        return 0;
    }

    public int getAmount() {
        return 0;
    }

    public int getDurability() {
        return 0;
    }
}
