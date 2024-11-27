package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.api.inventory.item.ItemStack;
import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public final class ClickWindowPacket implements Packet.INCOMING {
    private byte windowId;
    private short slot;
    private byte button;
    private short actionNumber;
    private byte mode;
    private ItemStack clickedItem;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        windowId = is.readByte();
        slot = is.readShort();
        button = is.readByte();
        actionNumber = is.readShort();
        mode = is.readByte();
        clickedItem = readSlot(is);
    }

}
