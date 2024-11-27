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
public final class CreativeInventoryActionPacket implements Packet.INCOMING {
    private short slot;
    private ItemStack clickedItem;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        slot = is.readShort();
        clickedItem = readSlot(is);
    }

}
