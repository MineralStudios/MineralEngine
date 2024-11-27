package gg.mineral.server.entity.object;

import gg.mineral.server.entity.EntityImpl;
import gg.mineral.server.world.WorldImpl;

public class Item extends EntityImpl {

    public Item(int id, WorldImpl world) {
        super(id, world);
    }

    @Override
    public void tickAsync() {
    }

}
