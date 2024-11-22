package gg.mineral.server.world;

import gg.mineral.server.world.IWorld.Environment;
import gg.mineral.server.world.IWorld.Generator;
import gg.mineral.server.world.chunk.Chunk;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import lombok.val;

import gg.mineral.server.MinecraftServer;

@RequiredArgsConstructor
public class WorldManager {
    private final Byte2ObjectOpenHashMap<World> worlds = new Byte2ObjectOpenHashMap<>();
    private final MinecraftServer server;

    public void init() {
        createWorld("Spawn", World.Environment.NORMAL, new World.Generator() {
            @Override
            public Chunk generate(IWorld world, byte chunkX, byte chunkZ) {
                val chunk = new Chunk(world, chunkX, chunkZ);

                for (int x = 0; x < 16; x++)
                    for (int z = 0; z < 16; z++)
                        chunk.setType(x, z, 50, 1);

                return chunk;
            }

        });
        System.out.println("[Mineral] Worlds initialized.");
    }

    public World getWorld(byte id) {
        return worlds.get(id);
    }

    public World createWorld(String name, Environment environment, Generator generator) {
        byte id = (byte) worlds.size();
        val world = new World(id, name, environment, generator, server);
        worlds.put(id, world);
        return world;
    }
}
