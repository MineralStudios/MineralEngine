package gg.mineral.server.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class Entity {
    @Setter
    @Getter
    float x, y, z, headY;
    @Setter
    @Getter
    float yaw, pitch;
    @Setter
    @Getter
    boolean onGround = false;
    @Getter
    protected final int id;

    public void tick() {

    }
}
