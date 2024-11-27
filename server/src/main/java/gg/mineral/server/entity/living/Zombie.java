package gg.mineral.server.entity.living;

import gg.mineral.server.entity.LivingImpl;
import gg.mineral.server.world.WorldImpl;

public class Zombie extends LivingImpl {

    public Zombie(int id, WorldImpl world) {
        super(id, world);
    }

    @Override
    public void tickAsync() {
    }

}
