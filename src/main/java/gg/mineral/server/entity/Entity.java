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
    protected double x, y, z, motX, motY, motZ;
    @Setter
    private double headY;
    protected float yaw, pitch, lastYaw, lastPitch;
    @Setter
    protected boolean onGround = false;
    @Getter
    private final byte viewDistance = (byte) 10;
    protected final int id;
    @Getter
    private int currentTick;
    @Getter
    @Setter
    private boolean firstTick = true, firstAsyncTick = true, chunkUpdateNeeded = true;
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

        if (motY < 0.005)
            motY = 0;

        if (motX < 0.005)
            motX = 0;

        if (motZ < 0.005)
            motZ = 0;

        if (motY > 0) {
            motY -= 0.08;
            motY *= 0.98;
        }

        if (motX > 0) {
            motX *= 0.91;
            if (onGround)
                motX *= 0.6;
        }

        if (motZ > 0) {
            motZ *= 0.91;
            if (onGround)
                motZ *= 0.6;
        }

    }

    public abstract void tickAsync();

    @Override
    public Entity call() {
        tickAsync();
        return this;
    }
}
