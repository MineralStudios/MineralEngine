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
public class ConfirmTransactionPacket implements Packet.INCOMING, Packet.OUTGOING {
    private short windowId, actionNumber;
    private boolean accepted;

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(windowId);
        os.writeShort(actionNumber);
        os.writeBoolean(accepted);
    }

    @Override
    public byte getId() {
        return 0x32;
    }

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        windowId = is.readByte();
        actionNumber = is.readShort();
        accepted = is.readBoolean();
    }

}
