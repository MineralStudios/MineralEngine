package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import io.netty.buffer.ByteBuf
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import lombok.experimental.Accessors

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
class SteerVehiclePacket : Packet.INCOMING {
    private var sideways = 0f
    private var forward = 0f
    private var jump = false
    private var unmount = false

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
