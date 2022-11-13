package gg.mineral.server.inventory.item;

public enum Material {

    UNKNOWN(-1); // TODO create materials

    int id;

    Material(int id) {
        this.id = id;
    }

    public static Material getById(int id) {
        for (Material material : Material.values()) {
            if (material.id == id) {
                return material;
            }
        }

        return null;
    }
}
