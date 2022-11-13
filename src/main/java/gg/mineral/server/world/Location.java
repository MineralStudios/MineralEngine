package gg.mineral.server.world;

public class Location {

    World world;
    double x, y, z;
    float yaw, pitch;

    public Location(World world, double x, double y, double z) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Location(World world, double x, double y, double z, float yaw, float pitch) {
        this(world, x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
    }

}
