package gg.mineral.server.entity.object;

import gg.mineral.server.entity.EntityImpl;
import gg.mineral.server.world.WorldImpl;

public class PoweredMinecart extends EntityImpl {

    public PoweredMinecart(int id, WorldImpl world) {
        super(id, world);
    }

    @Override
    public void tickAsync() {
    }

}
