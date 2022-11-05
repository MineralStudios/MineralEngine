package gg.mineral.server.network.packet.status.clientbound;

import gg.mineral.server.network.packet.OutgoingPacket;
import gg.mineral.server.network.ping.ServerPing;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;

public class ResponsePacket extends OutgoingPacket {

    String jsonResponse;

    public ResponsePacket(ServerPing serverPing) {
        this.jsonResponse = serverPing.toJsonString();
    }

    @Override
    public void serialize(ByteBuf os) {
        ByteBufUtil.writeString(os, jsonResponse);
    }

    @Override
    public int getId() {
        return 0x00;
    }

}
