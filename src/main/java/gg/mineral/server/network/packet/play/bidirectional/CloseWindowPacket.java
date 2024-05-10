package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class CloseWindowPacket implements Packet.INCOMING, Packet.OUTGOING {
    short windowId;

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(windowId);
    }

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        windowId = is.readByte();
    }

    @Override
    public byte getId() {
        return 0x2E;
    }

}
