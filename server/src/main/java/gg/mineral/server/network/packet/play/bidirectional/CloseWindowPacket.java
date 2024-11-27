package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(fluent = true)
public final class CloseWindowPacket implements Packet.INCOMING, Packet.OUTGOING {
    private short windowId;

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
