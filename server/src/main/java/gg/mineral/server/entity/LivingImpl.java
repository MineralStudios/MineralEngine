package gg.mineral.server.entity;

import gg.mineral.api.entity.Living;
import gg.mineral.api.world.World;
import gg.mineral.server.world.WorldImpl;

public class LivingImpl extends EntityImpl implements Living {

    public LivingImpl(int id, WorldImpl world) {
        super(id, world);
    }

    @Override
    public void tickAsync() {

    }

    @Override
    public void teleport(World world, double x, double y, double z, float yaw, float pitch) {
        if (world instanceof WorldImpl impl)
            this.setWorld(impl);
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    @Override
    public void teleport(World world, double x, double y, double z) {
        this.teleport(world, x, y, z, this.yaw, this.pitch);
    }
}