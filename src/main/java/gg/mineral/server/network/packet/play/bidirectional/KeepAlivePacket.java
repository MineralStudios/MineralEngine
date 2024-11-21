package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(fluent = true)
public class KeepAlivePacket implements Packet.INCOMING, Packet.OUTGOING {
    private int keepAliveId;

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(keepAliveId);
    }

    @Override
    public void received(Connection connection) {
        // connection.sendPacket(this);
    }

    @Override
    public void deserialize(ByteBuf is) {
        keepAliveId = is.readInt();
    }

    @Override
    public byte getId() {
        return 0x00;
    }

}
