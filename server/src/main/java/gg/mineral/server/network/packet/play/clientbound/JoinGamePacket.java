package gg.mineral.server.network.packet.play.clientbound;

import gg.mineral.api.entity.living.human.property.Gamemode;
import gg.mineral.api.network.packet.Packet;
import gg.mineral.api.world.property.Difficulty;
import gg.mineral.api.world.property.Dimension;
import gg.mineral.api.world.property.LevelType;
import io.netty.buffer.ByteBuf;

public final record JoinGamePacket(int entityId, Gamemode gamemode, Dimension dimension, Difficulty difficulty,
        short maxPlayers, LevelType levelType) implements Packet.OUTGOING {
    @Override
    public void serialize(ByteBuf os) {
        os.writeInt(entityId);
        os.writeByte(gamemode.getId());
        os.writeByte(dimension.getId());
        os.writeByte(difficulty.getId());
        os.writeByte(maxPlayers);
        writeString(os, levelType.string());
    }

    @Override
    public byte getId() {
        return 0x01;
    }
}
