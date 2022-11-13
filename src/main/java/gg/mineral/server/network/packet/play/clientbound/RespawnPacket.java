package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.entity.Gamemode;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import gg.mineral.server.world.Difficulty;
import gg.mineral.server.world.Dimension;
import gg.mineral.server.world.LevelType;
import io.netty.buffer.ByteBuf;

public class RespawnPacket implements Packet.OUTGOING {
    Dimension dimension;
    Difficulty difficulty;
    Gamemode gamemode;
    LevelType levelType;

    public RespawnPacket(Dimension dimension, Difficulty difficulty, Gamemode gamemode, LevelType levelType) {
        this.dimension = dimension;
        this.difficulty = difficulty;
        this.gamemode = gamemode;
        this.levelType = levelType;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(dimension.getId());
        os.writeByte(difficulty.getId());
        os.writeByte(gamemode.getId());
        ByteBufUtil.writeString(os, levelType.string());
    }

    @Override
    public int getId() {
        return 0x07;
    }

}
