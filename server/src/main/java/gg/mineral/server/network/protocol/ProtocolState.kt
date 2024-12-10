package gg.mineral.server.network.protocol

import gg.mineral.api.network.packet.Packet
import gg.mineral.api.network.packet.registry.PacketRegistry
import gg.mineral.server.network.packet.registry.PacketRegistryImpl
import gg.mineral.server.network.packet.status.serverbound.RequestPacket
import io.netty.util.AttributeKey
import java.util.concurrent.Callable

object ProtocolState {
    val ATTRIBUTE_KEY: AttributeKey<PacketRegistry<Packet.INCOMING>> = AttributeKey
        .valueOf("protocol_state")

    val HANDSHAKE: PacketRegistryImpl<Packet.INCOMING> = object : PacketRegistryImpl<Packet.INCOMING?>() {
        init {
            put(0x00.toByte(), Callable<Packet.INCOMING> { HandshakePacket() })
        }
    }

    val LOGIN: PacketRegistryImpl<Packet.INCOMING> = object : PacketRegistryImpl<Packet.INCOMING?>() {
        init {
            put(0x00.toByte(), Callable<Packet.INCOMING> { LoginStartPacket() })
            put(0x01.toByte(), Callable<Packet.INCOMING> { EncryptionKeyResponsePacket() })
        }
    }

    val PLAY: PacketRegistryImpl<Packet.INCOMING> = object : PacketRegistryImpl<Packet.INCOMING?>() {
        init {
            put(0x0A.toByte(), Callable<Packet.INCOMING> { AnimationPacket() })
            put(0x01.toByte(), Callable<Packet.INCOMING> { ChatMessagePacket() })
            put(0x09.toByte(), Callable<Packet.INCOMING> { HeldItemChangePacket() })
            put(0x00.toByte(), Callable<Packet.INCOMING> { KeepAlivePacket() })
            put(0x0E.toByte(), Callable<Packet.INCOMING> { ClickWindowPacket() })
            put(0x15.toByte(), Callable<Packet.INCOMING> { ClientSettingsPacket() })
            put(0x16.toByte(), Callable<Packet.INCOMING> { ClientStatusPacket() })
            put(0x0D.toByte(), Callable<Packet.INCOMING> { CloseWindowPacket() })
            put(0x0F.toByte(), Callable<Packet.INCOMING> { ConfirmTransactionPacket() })
            put(0x10.toByte(), Callable<Packet.INCOMING> { CreativeInventoryActionPacket() })
            put(0x11.toByte(), Callable<Packet.INCOMING> { EnchantItemPacket() })
            put(0x0B.toByte(), Callable<Packet.INCOMING> { EntityActionPacket() })
            put(0x13.toByte(), Callable<Packet.INCOMING> { PlayerAbilitiesPacket() })
            put(0x08.toByte(), Callable<Packet.INCOMING> { PlayerBlockPlacementPacket() })
            put(0x07.toByte(), Callable<Packet.INCOMING> { PlayerDiggingPacket() })
            put(0x05.toByte(), Callable<Packet.INCOMING> { PlayerLookPacket() })
            put(0x03.toByte(), Callable<Packet.INCOMING> { PlayerPacket() })
            put(0x06.toByte(), Callable<Packet.INCOMING> { PlayerPositionAndLookPacket() })
            put(0x04.toByte(), Callable<Packet.INCOMING> { PlayerPositionPacket() })
            put(0x17.toByte(), Callable<Packet.INCOMING> { PluginMessagePacket() })
            put(0x0C.toByte(), Callable<Packet.INCOMING> { SteerVehiclePacket() })
            put(0x14.toByte(), Callable<Packet.INCOMING> { TabCompletePacket() })
            put(0x12.toByte(), Callable<Packet.INCOMING> { UpdateSignPacket() })
            put(0x02.toByte(), Callable<Packet.INCOMING> { UseEntityPacket() })
        }
    }

    val STATUS: PacketRegistryImpl<Packet.INCOMING> = object : PacketRegistryImpl<Packet.INCOMING?>() {
        init {
            put(0x00.toByte(), Callable { RequestPacket() })
            put(0x01.toByte(), Callable<Packet.INCOMING> { PingPacket() })
        }
    }

    fun getState(i: Int): PacketRegistryImpl<Packet.INCOMING> {
        return if (i == 1) STATUS else LOGIN
    }
}
