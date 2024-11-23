package gg.mineral.server.network.packet.play.bidirectional;

import gg.mineral.server.entity.living.human.property.PlayerAbilities;
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
public class PlayerAbilitiesPacket implements Packet.INCOMING, Packet.OUTGOING {
    private byte flags;
    private float flyingSpeed, walkingSpeed;

    public PlayerAbilitiesPacket(PlayerAbilities playerAbilities) {
        this.flags = 0;

        if (playerAbilities.isInvulnerable())
            flags = (byte) (flags | 0x1);
        if (playerAbilities.flying())
            flags = (byte) (flags | 0x2);
        if (playerAbilities.canFly())
            flags = (byte) (flags | 0x4);
        if (playerAbilities.canInstantlyBuild())
            flags = (byte) (flags | 0x8);

        this.flyingSpeed = playerAbilities.flySpeed();
        this.walkingSpeed = playerAbilities.walkSpeed();
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeByte(flags);
        os.writeFloat(flyingSpeed);
        os.writeFloat(walkingSpeed);
    }

    @Override
    public void received(Connection connection) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deserialize(ByteBuf is) {
        flags = is.readByte();
        flyingSpeed = is.readFloat();
        walkingSpeed = is.readFloat();
    }

    @Override
    public byte getId() {
        return 0x39;
    }

}
