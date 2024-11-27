package gg.mineral.server.entity.living;

import gg.mineral.server.entity.LivingImpl;
import gg.mineral.server.world.WorldImpl;

public class Slime extends LivingImpl {

    public Slime(int id, WorldImpl world) {
        super(id, world);
    }

    @Override
    public void tickAsync() {
    }

}
