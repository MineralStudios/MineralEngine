package gg.mineral.server.network.packet.login.serverbound

import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.network.connection.ConnectionImpl
import io.netty.buffer.ByteBuf
import io.netty.channel.local.LocalChannel
import java.util.*

class FakePlayerInitializePacket(
    var name: String = "",
    var uuid: UUID = UUID.randomUUID(),
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var yaw: Float = 0.0f,
    var pitch: Float = 0.0f
) : Packet.Incoming,
    Packet.SyncHandler, Packet.ChannelWhitelist<LocalChannel>(LocalChannel::class) {
    override fun receivedSync(connection: Connection) {
        if (connection is ConnectionImpl) connection.attemptLogin(name, uuid)
    }

    override fun deserialize(`is`: ByteBuf) {
        this.name = `is`.readString()
        this.uuid = `is`.readUuid()
        this.x = `is`.readDouble()
        this.y = `is`.readDouble()
        this.z = `is`.readDouble()
        this.yaw = `is`.readFloat()
        this.pitch = `is`.readFloat()
    }
}
