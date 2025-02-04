package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf


class SteerVehiclePacket(
    var sideways: Float = 0f,
    var forward: Float = 0f,
    var jump: Boolean = false,
    var unmount: Boolean = false
) : Packet.Incoming {
    override fun deserialize(`is`: ByteBuf) {
        sideways = `is`.readFloat()
        forward = `is`.readFloat()
        jump = `is`.readBoolean()
        unmount = `is`.readBoolean()
    }
}
