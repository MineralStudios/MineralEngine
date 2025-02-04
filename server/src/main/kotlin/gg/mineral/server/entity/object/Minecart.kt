package gg.mineral.server.entity.`object`

import gg.mineral.server.entity.EntityImpl
import gg.mineral.server.snapshot.AsyncServerSnapshotImpl
import gg.mineral.server.world.WorldImpl

class Minecart(id: Int, serverSnapshot: AsyncServerSnapshotImpl, world: WorldImpl) :
    EntityImpl(id, serverSnapshot, world)
