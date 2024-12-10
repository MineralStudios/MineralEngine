package gg.mineral.server.network.packet.play.serverbound

import gg.mineral.api.entity.living.human.property.Gamemode
import gg.mineral.api.network.connection.Connection
import gg.mineral.api.network.packet.Packet
import gg.mineral.server.network.packet.play.clientbound.BlockChangePacket
import io.netty.buffer.ByteBuf
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import lombok.experimental.Accessors

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
class PlayerDiggingPacket : Packet.INCOMING {
    private var status: Byte = 0
    private var face: Byte = 0
    private var x = 0
    private var y: Short = 0
    private var z = 0

    override fun received(connection: Connection) {
        val player = connection.player ?: return

        if (status.toInt() == 2 || (status.toInt() == 0 && player.getGamemode() === Gamemode.CREATIVE)) { // done digging
            val world: Any = player.getWorld()
            val type: Int = world.getType(x, y, z)
            val meta: Int = world.getMetaData(x, y, z)
            connection.queuePacket(BlockChangePacket(x, y, z, type, meta.toShort()))
        }
    }

    override fun deserialize(`is`: ByteBuf) {
        status = `is`.readByte()
        x = `is`.readInt()
        y = `is`.readUnsignedByte()
        z = `is`.readInt()
        face = `is`.readByte()
    }
}
