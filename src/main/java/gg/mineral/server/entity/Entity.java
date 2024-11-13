package gg.mineral.server.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import java.util.Random;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Getter
public abstract class Entity implements Callable<Entity> {
    @Setter
    private double x, y, z, motX, motY, motZ;
    @Setter
    private double headY;
    private float yaw, pitch, lastYaw, lastPitch;
    @Setter
    private boolean onGround = false;
    protected final int id;
    @Getter
    private int currentTick;
    @Getter
    @Setter
    private boolean firstTick = true, firstAsyncTick = true;
    @Getter
    private final Random random = new Random(); // TODO: better random implementation

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

    public abstract void tickAsync();

    @Override
    public Entity call() {
        tickAsync();
        return this;
    }
}
