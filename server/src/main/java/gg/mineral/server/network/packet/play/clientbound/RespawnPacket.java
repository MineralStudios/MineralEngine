package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.entity.living.human.property.Gamemode;
import gg.mineral.api.network.packet.Packet;
import gg.mineral.api.world.property.Difficulty;
import gg.mineral.api.world.property.Dimension;
import gg.mineral.api.world.property.LevelType;
import io.netty.buffer.ByteBuf;

public final record RespawnPacket(Dimension dimension, Difficulty difficulty, Gamemode gamemode, LevelType levelType)
        implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(dimension.getId());
        os.writeByte(difficulty.getId());
        os.writeByte(gamemode.getId());
        writeString(os, levelType.string());
    }

    @Override
    public byte getId() {
        return 0x07;
    }
}
