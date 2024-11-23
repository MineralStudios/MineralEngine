package gg.mineral.server.world;

import java.io.File;

import gg.mineral.server.MinecraftServer;
import gg.mineral.server.world.IWorld.Environment;
import gg.mineral.server.world.IWorld.Generator;
import gg.mineral.server.world.chunk.Chunk;
import gg.mineral.server.world.schematic.Schematic;
import it.unimi.dsi.fastutil.bytes.Byte2ObjectOpenHashMap;
import lombok.RequiredArgsConstructor;
import lombok.val;

@RequiredArgsConstructor
public class WorldManager {
    private final Byte2ObjectOpenHashMap<World> worlds = new Byte2ObjectOpenHashMap<>();
    private final MinecraftServer server;
    private static final File worldFolder = new File("worlds");

    public void init() {
        if (!worldFolder.exists())
            worldFolder.mkdirs();

        for (val file : worldFolder.listFiles()) {
            if (file.getName().endsWith(".schematic")) {
                val name = file.getName().substring(0, file.getName().length() - 10);
                val schematic = Schematic.load(file);
                System.out.println("[Mineral] Loaded schematic " + name + " with " + schematic.getChunkedBlocks().size()
                        + " chunks.");

                createWorld(name, Environment.NORMAL, new Generator() {
                    @Override
                    public Chunk generate(IWorld world, byte chunkX, byte chunkZ) {
                        try {
                            val chunk = new Chunk(world, chunkX, chunkZ);

                            val blocks = schematic.getBlocksForChunk(chunkX, chunkZ);

                            for (val block : blocks) {
                                int x = block.getX(), y = block.getY(), z = block.getZ();
                                int localX = x & 15, localZ = z & 15;
                                chunk.setType(localX, localZ, y, block.getType());
                                chunk.setMetaData(localX, localZ, y, block.getData());
                            }

                            return chunk;
                        } catch (Exception e) {
                            e.printStackTrace();
                            return new Chunk(world, chunkX, chunkZ);
                        }
                    }
                });
            }
        }

        if (worlds.isEmpty())
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
