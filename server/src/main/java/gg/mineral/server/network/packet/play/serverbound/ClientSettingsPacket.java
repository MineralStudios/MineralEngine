package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;
import gg.mineral.api.world.property.Difficulty;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public final class ClientSettingsPacket implements Packet.INCOMING {
    private String locale;
    private byte viewDistance, chatFlags;
    private boolean chatColors;
    private Difficulty difficulty;
    private boolean showCape;

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        locale = readString(is);
        viewDistance = is.readByte();
        chatFlags = is.readByte();
        chatColors = is.readBoolean();
        difficulty = Difficulty.fromId(is.readByte());
        showCape = is.readBoolean();
    }
}
