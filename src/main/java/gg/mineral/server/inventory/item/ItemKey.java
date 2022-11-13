package gg.mineral.server.inventory.item;

public class ItemKey {
    String prefix, name;

    public ItemKey(String prefix, String name) {
        this.prefix = prefix;
        this.name = name;
    }

    public ItemKey(String name) {
        this("minecraft", name);
    }

    public String toString() {
        return this.prefix + ':' + this.name;
    }

    public int hashCode() {
        return 31 * this.prefix.hashCode() + this.name.hashCode();
    }
}
