package gg.mineral.server.network.packet.status.bidirectional;

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
public class PingPacket implements Packet.INCOMING, Packet.OUTGOING {

    long time;

    @Override
    public byte getId() {
        return 0x01;
    }

    @Override
    public void received(Connection connection) {
        connection.sendPacket(this);
    }

    @Override
    public void deserialize(ByteBuf is) {
        time = is.readLong();
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeLong(time);
    }

}
