package gg.mineral.server.world;

public enum Difficulty {
    PEACEFUL((byte) 0), EASY((byte) 1), NORMAL((byte) 2), HARD((byte) 3);

    byte id;

    Difficulty(byte id) {
        this.id = id;
    }

    public byte getId() {
        return id;
    }
}
