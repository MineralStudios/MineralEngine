package gg.mineral.server.entity.living;

import gg.mineral.server.entity.LivingImpl;
import gg.mineral.server.world.WorldImpl;

public class Skeleton extends LivingImpl {

    public Skeleton(int id, WorldImpl world) {
        super(id, world);
    }

    @Override
    public void tickAsync() {
    }

}
