package gg.mineral.server.entity.living.human.property;

import gg.mineral.server.util.nbt.CompoundTag;

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

    public void writeNbt(CompoundTag compoundTag) {
        CompoundTag abilitiesCompoundTag = new CompoundTag();
        abilitiesCompoundTag.putBool("invulnerable", this.isInvulnerable);
        abilitiesCompoundTag.putBool("flying", this.isFlying);
        abilitiesCompoundTag.putBool("mayfly", this.canFly);
        abilitiesCompoundTag.putBool("instabuild", this.canInstantlyBuild);
        abilitiesCompoundTag.putBool("mayBuild", this.mayBuild);
        abilitiesCompoundTag.putFloat("flySpeed", this.flySpeed);
        abilitiesCompoundTag.putFloat("walkSpeed", this.walkSpeed);
        compoundTag.putCompound("abilities", abilitiesCompoundTag);
    }

    public void readNbt(CompoundTag compoundTag) {
        if (compoundTag.containsKey("abilities")) {
            CompoundTag abilitiesCompoundTag = compoundTag.getCompound("abilities");
            this.isInvulnerable = abilitiesCompoundTag.getBool("invulnerable");
            this.isFlying = abilitiesCompoundTag.getBool("flying");
            this.canFly = abilitiesCompoundTag.getBool("mayfly");
            this.canInstantlyBuild = abilitiesCompoundTag.getBool("instabuild");
            if (abilitiesCompoundTag.containsKey("flySpeed")) {
                this.flySpeed = abilitiesCompoundTag.getFloat("flySpeed");
                this.walkSpeed = abilitiesCompoundTag.getFloat("walkSpeed");
            }
            if (abilitiesCompoundTag.containsKey("mayBuild"))
                this.mayBuild = abilitiesCompoundTag.getBool("mayBuild");
        }
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
