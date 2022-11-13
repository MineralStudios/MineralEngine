package gg.mineral.server.world.property;

public enum Dimension {
    NETHER((byte) -1), OVERWORLD((byte) 0), END((byte) 1);

    byte id;

    Dimension(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }
}
