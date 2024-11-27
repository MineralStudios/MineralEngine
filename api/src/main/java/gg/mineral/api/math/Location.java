package gg.mineral.api.math;

import gg.mineral.api.world.World;
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
