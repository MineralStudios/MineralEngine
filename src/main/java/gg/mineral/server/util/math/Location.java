package gg.mineral.server.util.math;

import gg.mineral.server.world.World;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class Location {
    private final World world;
    private final double x, y, z;
    private float yaw, pitch;
}
