package gg.mineral.server.network;

import java.util.List;

import gg.mineral.server.network.packet.BidirectionalPacket;
import gg.mineral.server.network.packet.IncomingPacket;
import gg.mineral.server.network.packet.IncomingPacketRegistry;
import gg.mineral.server.network.packet.OutgoingPacket;
import gg.mineral.server.network.protocol.ProtocolState;
import gg.mineral.server.network.protocol.ProtocolVersion;
import gg.mineral.server.MinecraftServer;
import gg.mineral.server.util.collection.GlueList;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class Connection extends SimpleChannelInboundHandler<IncomingPacket> {
    public static List<Connection> LIST = new GlueList<>();
    IncomingPacketRegistry protocolState = ProtocolState.HANDSHAKE;
    public int PROTOCOL_VERSION = ProtocolVersion.V1_7_6;
    Channel channel;

    public void sendPacket(OutgoingPacket... packets) {
        for (OutgoingPacket packet : packets) {
            channel.write(packet.write());

            if (MinecraftServer.DEBUG_MESSAGES)
                System.out.println("[Mineral] Packet sent: " + packet.getClass().getSimpleName());
        }

        channel.flush();
    }

    public void channelActive(ChannelHandlerContext channelhandlercontext) throws Exception {
        super.channelActive(channelhandlercontext);
        this.channel = channelhandlercontext.channel();

        setProtocolState(ProtocolState.HANDSHAKE);
    }

    public IncomingPacketRegistry getProtocolState() {
        return protocolState;
    }

    public void setProtocolState(IncomingPacketRegistry protocolState) {
        this.protocolState = protocolState;
        this.channel.attr(ProtocolState.ATTRIBUTE_KEY).set(protocolState);
    }

    public void sendPacket(BidirectionalPacket... packets) {
        for (BidirectionalPacket packet : packets) {
            channel.write(packet.write());

            if (MinecraftServer.DEBUG_MESSAGES)
                System.out.println("[Mineral] Packet sent: " + packet.getClass().getSimpleName());
        }

        channel.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IncomingPacket received) throws Exception {
        if (MinecraftServer.DEBUG_MESSAGES)
            System.out.println("[Mineral] Packet received: " + received.getClass().getSimpleName());
        received.received(this);
    }
}
