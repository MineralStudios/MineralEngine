package gg.mineral.server.entity

import gg.mineral.server.snapshot.AsyncServerSnapshotImpl
import gg.mineral.server.world.WorldImpl

abstract class Ageable(id: Int, serverSnapshot: AsyncServerSnapshotImpl, world: WorldImpl) :
    EntityImpl(id, serverSnapshot, world)
