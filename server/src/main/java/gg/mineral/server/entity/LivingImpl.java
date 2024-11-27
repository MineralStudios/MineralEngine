package gg.mineral.server.entity;

import gg.mineral.api.entity.Living;
import gg.mineral.server.world.WorldImpl;

public class LivingImpl extends EntityImpl implements Living {

    public LivingImpl(int id, WorldImpl world) {
        super(id, world);
    }

    @Override
    public void tickAsync() {

    }

}