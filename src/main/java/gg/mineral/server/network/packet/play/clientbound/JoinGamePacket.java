package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.server.entity.Gamemode;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.util.network.ByteBufUtil;
import gg.mineral.server.world.Difficulty;
import gg.mineral.server.world.Dimension;
import gg.mineral.server.world.LevelType;
import io.netty.buffer.ByteBuf;

public class JoinGamePacket implements Packet.OUTGOING {

    int entityId;
    Gamemode gamemode;
    Difficulty difficulty;
    Dimension dimension;
    LevelType levelType;
    short maxPlayers;

    public JoinGamePacket(int entityId, Gamemode gamemode, Dimension dimension, Difficulty difficulty,
            LevelType levelType, short maxPlayers) {
        this.entityId = entityId;
        this.gamemode = gamemode;
        this.dimension = dimension;
        this.levelType = levelType;
        this.difficulty = difficulty;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeByte(gamemode.getId());
        os.writeByte(dimension.getId());
        os.writeByte(difficulty.getId());
        os.writeByte(maxPlayers);
        ByteBufUtil.writeString(os, levelType.string());
    }

    @Override
    public int getId() {
        return 0x01;
    }

}
