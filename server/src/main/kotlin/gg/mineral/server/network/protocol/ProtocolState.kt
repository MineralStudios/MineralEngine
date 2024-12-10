package gg.mineral.server.network.protocol

import gg.mineral.api.network.packet.Packet
import gg.mineral.api.network.packet.registry.PacketRegistry
import gg.mineral.server.network.packet.handshake.serverbound.HandshakePacket
import gg.mineral.server.network.packet.login.serverbound.EncryptionKeyResponsePacket
import gg.mineral.server.network.packet.login.serverbound.LoginStartPacket
import gg.mineral.server.network.packet.play.bidirectional.*
import gg.mineral.server.network.packet.play.serverbound.*
import gg.mineral.server.network.packet.registry.PacketRegistryImpl
import gg.mineral.server.network.packet.status.bidirectional.PingPacket
import gg.mineral.server.network.packet.status.serverbound.RequestPacket
import io.netty.util.AttributeKey

object ProtocolState {
    val ATTRIBUTE_KEY: AttributeKey<PacketRegistry<Packet.INCOMING>> = AttributeKey
        .valueOf("protocol_state")

    val HANDSHAKE: PacketRegistryImpl<Packet.INCOMING> = object : PacketRegistryImpl<Packet.INCOMING>() {
        init {
            put(0x00.toByte(), ::HandshakePacket)
        }
    }

    val LOGIN: PacketRegistryImpl<Packet.INCOMING> = object : PacketRegistryImpl<Packet.INCOMING>() {
        init {
            put(0x00.toByte(), ::LoginStartPacket)
            put(0x01.toByte(), ::EncryptionKeyResponsePacket)
        }
    }

    val PLAY: PacketRegistryImpl<Packet.INCOMING> = object : PacketRegistryImpl<Packet.INCOMING>() {
        init {
            put(0x0A.toByte(), ::AnimationPacket)
            put(0x01.toByte(), ::ChatMessagePacket)
            put(0x09.toByte(), ::HeldItemChangePacket)
            put(0x00.toByte(), ::KeepAlivePacket)
            put(0x0E.toByte(), ::ClickWindowPacket)
            put(0x15.toByte(), ::ClientSettingsPacket)
            put(0x16.toByte(), ::ClientStatusPacket)
            put(0x0D.toByte(), ::CloseWindowPacket)
            put(0x0F.toByte(), ::ConfirmTransactionPacket)
            put(0x10.toByte(), ::CreativeInventoryActionPacket)
            put(0x11.toByte(), ::EnchantItemPacket)
            put(0x0B.toByte(), ::EntityActionPacket)
            put(0x13.toByte(), ::PlayerAbilitiesPacket)
            put(0x08.toByte(), ::PlayerBlockPlacementPacket)
            put(0x07.toByte(), ::PlayerDiggingPacket)
            put(0x05.toByte(), ::PlayerLookPacket)
            put(0x03.toByte(), ::PlayerPacket)
            put(0x06.toByte(), ::PlayerPositionAndLookPacket)
            put(0x04.toByte(), ::PlayerPositionPacket)
            put(0x17.toByte(), ::PluginMessagePacket)
            put(0x0C.toByte(), ::SteerVehiclePacket)
            put(0x14.toByte(), ::TabCompletePacket)
            put(0x12.toByte(), ::UpdateSignPacket)
            put(0x02.toByte(), ::UseEntityPacket)
        }
    }

    val STATUS: PacketRegistryImpl<Packet.INCOMING> = object : PacketRegistryImpl<Packet.INCOMING>() {
        init {
            put(0x00.toByte(), ::RequestPacket)
            put(0x01.toByte(), ::PingPacket)
        }
    }

    fun getState(i: Int): PacketRegistryImpl<Packet.INCOMING> {
        return if (i == 1) STATUS else LOGIN
    }
}
