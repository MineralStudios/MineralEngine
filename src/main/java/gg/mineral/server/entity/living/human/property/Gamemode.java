package gg.mineral.server.entity.living.human.property;

public enum Gamemode {
    SURVIVAL((short) 0), CREATIVE((short) 1), ADVENTURE((short) 2), HARDCORE((short) 0x8);

    short id;

    Gamemode(short id) {
        this.id = id;
    }

    public short getId() {
        return id;
    }
}
