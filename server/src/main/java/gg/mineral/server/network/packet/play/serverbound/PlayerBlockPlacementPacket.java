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
public class PlayerBlockPlacementPacket implements Packet.INCOMING {
    private int x;
    private short y;
    private int z;
    private byte direction;
    private ItemStack itemStack;
    private byte cursorX, cursorY, cursorZ;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        x = is.readInt();
        y = is.readUnsignedByte();
        z = is.readInt();
        direction = is.readByte();
        itemStack = ByteBufUtil.readSlot(is);
        cursorX = is.readByte();
        cursorY = is.readByte();
        cursorZ = is.readByte();
    }

}
