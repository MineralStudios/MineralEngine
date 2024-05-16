package gg.mineral.server.world;

import gg.mineral.server.world.World.Environment;
import gg.mineral.server.world.World.Generator;
import gg.mineral.server.world.chunk.Chunk;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;

public class WorldManager {
    static final Byte2ObjectOpenHashMap<World> worlds = new Byte2ObjectOpenHashMap<>();

    static {
        createWorld("Spawn", World.Environment.NORMAL, new World.Generator() {
            @Override
            public Chunk generate(World world, byte chunkX, byte chunkZ) {
                return new Chunk(world, chunkX, chunkZ) {

                    @Override
                    public void load() {
                        for (int x1 = 0; x1 < 16; x1++)
                            for (int z1 = 0; z1 < 16; z1++)
                                setType(x1, z1, 50, 1);
                    }

                };
            }

            @Override
            public boolean pregenerate() {
                return true;
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
        World world = new World(id, name, environment, generator);
        if (generator.pregenerate())
            for (byte x = World.MIN_CHUNK_COORD; x < World.MAX_CHUNK_COORD; x++)
                for (byte z = World.MIN_CHUNK_COORD; z < World.MAX_CHUNK_COORD; z++)
                    world.getChunk(Chunk.toKey(x, z));
        worlds.put(id, world);
        return world;
    }
}
