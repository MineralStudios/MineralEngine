package gg.mineral.api.entity.living.human.property

class PlayerAbilities(
    val isInvulnerable: Boolean = false,
    val flying: Boolean = false,
    val canFly: Boolean = false,
    val canInstantlyBuild: Boolean = false,
    val mayBuild: Boolean = true,
    val flySpeed: Float = 0.05f,
    val walkSpeed: Float = 0.1f
)
