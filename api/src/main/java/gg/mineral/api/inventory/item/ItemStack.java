package gg.mineral.api.inventory.item;

import java.util.ArrayList;
import java.util.List;

import gg.mineral.api.inventory.item.enchant.Enchantment;
import gg.mineral.api.nbt.CompoundTag;
import gg.mineral.api.nbt.TagType;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;

@RequiredArgsConstructor
public class ItemStack {
    @Getter
    @Setter
    private String displayName;
    @Getter
    @Setter
    private List<String> lore;
    private Object2IntOpenHashMap<Enchantment> enchants;
    private int hideFlag;
    @Getter
    private final short typeId;
    @Getter
    private short amount, durability;

    public ItemStack(short typeId, short amount, short durability) {
        this.typeId = typeId;
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
        val displayTags = new CompoundTag();
        if (hasDisplayName())
            displayTags.putString("Name", getDisplayName());

        if (hasLore())
            displayTags.putList("Lore", TagType.STRING, getLore());

        if (!displayTags.isEmpty())
            tag.putCompound("display", displayTags);

        if (hasEnchants())
            writeNbtEnchants("ench", tag, enchants);

        if (hideFlag != 0)
            tag.putInt("HideFlags", hideFlag);
    }

    public void readNbt(CompoundTag tag) {
        if (tag == null)
            return;
        if (tag.isCompound("display")) {
            val display = tag.getCompound("display");
            if (display.isString("Name"))
                setDisplayName(display.getString("Name"));

            if (display.isList("Lore", TagType.STRING))
                setLore(display.<String>getList("Lore", TagType.STRING));

        }

        val tagEnchants = readNbtEnchants("ench", tag);
        if (tagEnchants != null) {
            if (enchants == null)
                enchants = tagEnchants;
            else
                enchants.putAll(tagEnchants);
        }

        if (tag.isInt("HideFlags"))
            hideFlag = tag.getInt("HideFlags");

    }

    protected static void writeNbtEnchants(String name, CompoundTag to,
            Object2IntOpenHashMap<Enchantment> enchants) {
        val ench = new ArrayList<CompoundTag>();

        for (val enchantment : enchants.object2IntEntrySet()) {
            val enchantmentTag = new CompoundTag();
            enchantmentTag.putShort("id", enchantment.getKey().getId());
            enchantmentTag.putShort("lvl", enchantment.getIntValue());
            ench.add(enchantmentTag);
        }

        to.putCompoundList(name, ench);
    }

    protected static Object2IntOpenHashMap<Enchantment> readNbtEnchants(String name, CompoundTag tag) {
        Object2IntOpenHashMap<Enchantment> result = null;

        if (tag.isList(name, TagType.COMPOUND)) {
            val enchs = tag.getCompoundList(name);
            for (val enchantmentTag : enchs) {
                if (enchantmentTag.isShort("id") && enchantmentTag.isShort("lvl")) {
                    val enchantment = Enchantment.getById(enchantmentTag.getShort("id"));
                    if (result == null)
                        result = new Object2IntOpenHashMap<>(4);
                    result.put(enchantment, (int) enchantmentTag.getShort("lvl"));
                }
            }
        }

        return result;
    }
}
