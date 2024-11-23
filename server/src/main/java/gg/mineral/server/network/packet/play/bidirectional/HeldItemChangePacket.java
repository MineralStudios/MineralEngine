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
public class HeldItemChangePacket implements Packet.INCOMING, Packet.OUTGOING {
    private short slot;

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(slot);
    }

    @Override
    public byte getId() {
        return 0x09;
    }

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        slot = is.readShort();
    }

}
