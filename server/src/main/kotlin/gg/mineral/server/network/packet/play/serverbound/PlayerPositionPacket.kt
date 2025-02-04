package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import io.netty.buffer.ByteBuf

open class PlayerPositionPacket(
    open var x: Double = 0.0,
    open var feetY: Double = 0.0,
    open var headY: Double = 0.0,
    open var z: Double = 0.0,
    onGround: Boolean = false
) : PlayerPacket(onGround) {
    override suspend fun receivedSync(connection: Connection) {
        connection.player?.let {
            it.motX = x - it.x
            it.motY = feetY - it.y
            it.motZ = z - it.z
            it.x = x
            it.y = feetY
            it.headY = headY
            it.z = z
        }

        super.receivedSync(connection)
    }

    override fun deserialize(`is`: ByteBuf) {
        x = `is`.readDouble()
        feetY = `is`.readDouble()
        headY = `is`.readDouble()
        z = `is`.readDouble()
        super.deserialize(`is`)
    }
}
