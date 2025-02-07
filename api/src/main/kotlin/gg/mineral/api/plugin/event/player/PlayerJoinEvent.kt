package gg.mineral.api.plugin.event.player

import gg.mineral.api.entity.living.human.Player

class PlayerJoinEvent(
    player: Player,
    var x: Double = player.x,
    var y: Double = player.y,
    var z: Double = player.z,
    var yaw: Float = player.yaw,
    var pitch: Float = player.pitch
) :
    PlayerEvent(player)
