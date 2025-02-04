package gg.mineral.server.entity

import gg.mineral.api.entity.Living
import gg.mineral.api.world.World
import gg.mineral.server.snapshot.AsyncServerSnapshotImpl
import gg.mineral.server.world.WorldImpl

open class LivingImpl(id: Int, serverSnapshot: AsyncServerSnapshotImpl, world: WorldImpl) :
    EntityImpl(id, serverSnapshot, world), Living {
    override suspend fun tick() {
        super.tick()

        if (motY < 0.005) motY = 0.0

        if (motX < 0.005) motX = 0.0

        if (motZ < 0.005) motZ = 0.0

        if (motY > 0) {
            motY -= 0.08
            motY *= 0.98
        }

        if (motX > 0) {
            motX *= 0.91
            if (onGround) motX *= 0.6
        }

        if (motZ > 0) {
            motZ *= 0.91
            if (onGround) motZ *= 0.6
        }
    }

    override fun teleport(world: World, x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
        if (world is WorldImpl) this.world = world
        this.x = x
        this.y = y
        this.z = z
        this.yaw = yaw
        this.pitch = pitch
    }

    override fun teleport(world: World, x: Double, y: Double, z: Double) =
        this.teleport(world, x, y, z, this.yaw, this.pitch)
}