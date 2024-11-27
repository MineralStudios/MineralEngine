package gg.mineral.server.entity;

import java.util.Random;
import java.util.concurrent.Callable;

import gg.mineral.api.entity.Entity;
import gg.mineral.api.math.MathUtil;
import gg.mineral.server.MinecraftServerImpl;
import gg.mineral.server.command.impl.KnockbackCommand;
import gg.mineral.server.entity.living.HumanImpl;
import gg.mineral.server.entity.living.human.PlayerImpl;
import gg.mineral.server.network.packet.play.clientbound.EntityStatusPacket;
import gg.mineral.server.network.packet.play.clientbound.EntityVelocityPacket;
import gg.mineral.server.network.packet.play.clientbound.SoundEffectPacket;
import gg.mineral.server.world.WorldImpl;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

@Getter
public abstract class EntityImpl implements Callable<EntityImpl>, Entity, MathUtil {
    @Setter
    protected double x, y, z, motX, motY, motZ, headY;
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
    @Setter
    private int lastDamaged;
    @Getter
    private final Random random = new Random(); // TODO: better random implementation
    protected float width, height;
    @Getter
    protected WorldImpl world;
    protected final MinecraftServerImpl server;

    public EntityImpl(int id, WorldImpl world) {
        this.id = id;
        this.setWorld(world);
        this.server = world.getServer();
    }

    public void setWorld(WorldImpl world) {
        val oldWorld = getWorld();

        if (oldWorld != null)
            oldWorld.removeEntity(this.getId());
        world.addEntity(this);
        this.world = world;
    }

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
    public EntityImpl call() {
        tickAsync();
        return this;
    }

    @Override
    public void attack(int targetId) {
        val entity = server.getEntities().get(targetId);

        if (entity == null || entity.getCurrentTick() - entity.getLastDamaged() < 10)
            return;

        entity.setLastDamaged(entity.getCurrentTick());
        val statusPacket = new EntityStatusPacket(entity.getId(), (byte) 2);

        double motX = entity.getMotX();
        double motY = entity.getMotY();
        double motZ = entity.getMotZ();
        double x = KnockbackCommand.x;
        double y = KnockbackCommand.y;
        double z = KnockbackCommand.z;

        double extraX = KnockbackCommand.extraX;
        double extraY = KnockbackCommand.extraY;
        double extraZ = KnockbackCommand.extraZ;

        double yLimit = KnockbackCommand.yLimit;

        double friction = KnockbackCommand.friction;

        if (friction > 0) {
            motX /= friction;
            motY /= friction;
            motZ /= friction;
        } else {
            motX = 0;
            motY = 0;
            motZ = 0;
        }

        double distanceX = this.getX() - entity.getX();
        double distanceZ = this.getZ() - entity.getZ();

        double magnitude = Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);

        motX -= distanceX / magnitude * x;
        motY += y;
        motZ -= distanceZ / magnitude * z;

        if (motY > yLimit)
            motY = yLimit;

        if (this instanceof HumanImpl human) {
            if (human.isExtraKnockback()) {
                float angle = (float) Math.toRadians(this.getYaw());
                double sin = -Math.sin(angle);
                double cos = Math.cos(angle);
                motX += extraX * sin;
                motY += extraY;
                motZ += extraZ * cos;
                this.setMotX(this.getMotX() * 0.6);
                this.setMotZ(this.getMotZ() * 0.6);
                human.setExtraKnockback(false);
            }
        }
        val velocityPacket = new EntityVelocityPacket(targetId, toVelocityUnits(motX),
                toVelocityUnits(motY), toVelocityUnits(motZ));

        if (entity instanceof PlayerImpl player)
            player.getConnection().queuePacket(statusPacket, velocityPacket);

        if (this instanceof PlayerImpl player)
            player.getConnection().queuePacket(statusPacket, new SoundEffectPacket("game.player.hurt",
                    toSoundUnits(entity.getX()), toSoundUnits(entity.getY()),
                    toSoundUnits(entity.getZ()), 1.0f, toPitchUnits((this.getRandom().nextFloat()
                            - this.getRandom().nextFloat()) * 0.2F + 1.0F)));
        entity.setMotX(motX);
        entity.setMotY(motY);
        entity.setMotZ(motZ);
    }
}
