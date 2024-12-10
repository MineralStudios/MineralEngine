package gg.mineral.server.entity

import gg.mineral.api.entity.Living
import gg.mineral.api.world.World
import gg.mineral.server.world.WorldImpl

open class LivingImpl(id: Int, world: WorldImpl) : EntityImpl(id, world), Living {
    override fun tickAsync() {
    }

    override fun teleport(world: World, x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        if (world is WorldImpl) this.world = world
        this.x = x
        this.y = y
        this.z = z
        this.yaw = yaw
        this.pitch = pitch
    }

    override fun teleport(world: World, x: Double, y: Double, z: Double) {
        this.teleport(world, x, y, z, this.yaw, this.pitch)
    }
}