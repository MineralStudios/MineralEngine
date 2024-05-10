package gg.mineral.server.entity.living.human.property;

public class PlayerAbilities {
    boolean isInvulnerable, isFlying, canFly, canInstantlyBuild, mayBuild = true;
    float flySpeed = 0.05f, walkSpeed = 0.1f;

    public PlayerAbilities(boolean isInvulnerable, boolean isFlying, boolean canFly, boolean canInstantlyBuild,
            boolean mayBuild, float flySpeed, float walkSpeed) {
        this.isInvulnerable = isInvulnerable;
        this.isFlying = isFlying;
        this.canFly = canFly;
        this.canInstantlyBuild = canInstantlyBuild;
        this.mayBuild = mayBuild;
        this.flySpeed = flySpeed;
        this.walkSpeed = walkSpeed;
    }

    public float flySpeed() {
        return this.flySpeed;
    }

    public float walkSpeed() {
        return this.walkSpeed;
    }

    public boolean isFlying() {
        return this.isFlying;
    }

    public boolean canFly() {
        return this.canFly;
    }

    public boolean canInstantlyBuild() {
        return this.canInstantlyBuild;
    }

    public boolean mayBuild() {
        return this.mayBuild;
    }

    public boolean isInvulnerable() {
        return this.isInvulnerable;
    }
}
