package gg.mineral.server.world;

import gg.mineral.server.world.IWorld.Environment;
import gg.mineral.server.world.IWorld.Generator;
import gg.mineral.server.world.chunk.Chunk;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import lombok.val;

public class WorldManager {
    static final Byte2ObjectOpenHashMap<World> worlds = new Byte2ObjectOpenHashMap<>();

    static {
        createWorld("Spawn", World.Environment.NORMAL, new World.Generator() {
            @Override
            public Chunk generate(Environment environment, byte chunkX, byte chunkZ) {
                val chunk = new Chunk(environment, chunkX, chunkZ);

                for (int x = 0; x < 16; x++)
                    for (int z = 0; z < 16; z++)
                        chunk.setType(x, z, 50, 1);
                return chunk;
            }

        });
    }

    public static void init() {
        System.out.println("[Mineral] Worlds initialized.");
    }

    public static World getWorld(byte id) {
        return worlds.get(id);
    }

    public static World createWorld(String name, Environment environment, Generator generator) {
        byte id = (byte) worlds.size();
        val world = new World(id, name, environment, generator);
        worlds.put(id, world);
        return world;
    }
}
