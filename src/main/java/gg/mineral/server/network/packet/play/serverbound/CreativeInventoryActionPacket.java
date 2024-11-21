package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.inventory.item.ItemStack;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public class CreativeInventoryActionPacket implements Packet.INCOMING {
    private short slot;
    private ItemStack clickedItem;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        slot = is.readShort();
        clickedItem = ByteBufUtil.readSlot(is);
    }

}
