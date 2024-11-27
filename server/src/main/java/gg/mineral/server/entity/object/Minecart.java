package gg.mineral.server.entity.object;

import gg.mineral.server.entity.EntityImpl;
import gg.mineral.server.world.WorldImpl;

public class Minecart extends EntityImpl {

    public Minecart(int id, WorldImpl world) {
        super(id, world);
    }

    @Override
    public void tickAsync() {
    }

}
