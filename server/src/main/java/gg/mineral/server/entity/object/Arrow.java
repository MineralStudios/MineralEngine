package gg.mineral.server.entity.object;

import gg.mineral.server.entity.EntityImpl;
import gg.mineral.server.world.WorldImpl;

public class Arrow extends EntityImpl {

    public Arrow(int id, WorldImpl world) {
        super(id, world);
    }

    @Override
    public void tickAsync() {
    }

}
