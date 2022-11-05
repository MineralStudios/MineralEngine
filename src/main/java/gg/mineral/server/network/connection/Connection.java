package gg.mineral.server.network.connection;

import java.util.List;

import gg.mineral.server.MinecraftServer;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.network.packet.registry.IncomingPacketRegistry;
import gg.mineral.server.network.protocol.ProtocolState;
import gg.mineral.server.network.protocol.ProtocolVersion;
import gg.mineral.server.util.collection.GlueList;
import gg.mineral.server.util.network.PacketUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class Connection extends SimpleChannelInboundHandler<Packet.INCOMING> {
    public static List<Connection> LIST = new GlueList<>();
    IncomingPacketRegistry protocolState = ProtocolState.HANDSHAKE;
    public int PROTOCOL_VERSION = ProtocolVersion.V1_7_6;
    Channel channel;

    public void sendPacket(Packet.OUTGOING... packets) {
        for (Packet.OUTGOING packet : packets) {
            channel.write(PacketUtil.serialize(packet));

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

    public void close() {
        channel.close();
    }

    public IncomingPacketRegistry getProtocolState() {
        return protocolState;
    }

    public void setProtocolState(IncomingPacketRegistry protocolState) {
        this.protocolState = protocolState;
        this.channel.attr(ProtocolState.ATTRIBUTE_KEY).set(protocolState);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Packet.INCOMING received) throws Exception {
        if (MinecraftServer.DEBUG_MESSAGES)
            System.out.println("[Mineral] Packet received: " + received.getClass().getSimpleName());
        received.received(this);
    }
}
