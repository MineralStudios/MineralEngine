package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf


class SteerVehiclePacket(
    var sideways: Float = 0f,
    var forward: Float = 0f,
    var jump: Boolean = false,
    var unmount: Boolean = false
) : Packet.INCOMING {
    override fun received(connection: Connection) {
        // TODO Auto-generated method stub
    }

    override fun deserialize(`is`: ByteBuf) {
        sideways = `is`.readFloat()
        forward = `is`.readFloat()
        jump = `is`.readBoolean()
        unmount = `is`.readBoolean()
    }
}
