package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.api.network.connection.Connection;
import gg.mineral.api.network.packet.Packet;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.val;
import lombok.experimental.Accessors;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Accessors(fluent = true)
public final class UseEntityPacket implements Packet.INCOMING {
    private int target;
    private byte mouse;

    @Override
    public void received(Connection connection) {
        if (mouse == 1) { // left click
            if (target != -1) {
                val attacker = connection.getPlayer();

                if (attacker != null)
                    attacker.attack(target);
            }
        }
    }

    @Override
    public void deserialize(ByteBuf is) {
        target = is.readInt();
        mouse = is.readByte();
    }
}
