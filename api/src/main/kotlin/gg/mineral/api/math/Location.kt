package gg.mineral.api.math

import gg.mineral.api.world.World

class Location(
    val world: World,
    val x: Double,
    val y: Double,
    val z: Double,
    val yaw: Float = 0.0f,
    val pitch: Float = 0.0f
)

