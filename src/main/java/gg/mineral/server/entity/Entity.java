package gg.mineral.server.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.Random;

@RequiredArgsConstructor
@Getter
public class Entity {
    @Setter
    double x, y, z, motX, motY, motZ;
    @Setter
    double headY;
    float yaw, pitch, lastYaw, lastPitch;
    @Setter
    boolean onGround = false;
    protected final int id;
    @Getter
    int currentTick;
    @Getter
    final Random random = new Random(); // TODO: better random implementation

    public void setYaw(float yaw) {
        lastYaw = this.yaw;
        this.yaw = yaw;
    }

    public void setPitch(float pitch) {
        lastPitch = this.pitch;
        this.pitch = pitch;
    }

    public void tick() {
        currentTick++;
    }
}
