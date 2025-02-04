package gg.mineral.server.entity.living

import gg.mineral.server.entity.LivingImpl
import gg.mineral.server.snapshot.AsyncServerSnapshotImpl
import gg.mineral.server.world.WorldImpl

class Zombie(id: Int, serverSnapshot: AsyncServerSnapshotImpl, world: WorldImpl) : LivingImpl(id, serverSnapshot, world)
