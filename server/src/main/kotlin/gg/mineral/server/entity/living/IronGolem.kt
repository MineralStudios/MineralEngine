package gg.mineral.server.entity.living

import gg.mineral.server.entity.LivingImpl
import gg.mineral.server.world.WorldImpl

class IronGolem(id: Int, world: WorldImpl) : LivingImpl(id, world) {
    override fun tickAsync() {
    }
}
