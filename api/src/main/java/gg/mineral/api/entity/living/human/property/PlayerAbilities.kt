package gg.mineral.api.entity.living.human.property

import lombok.AllArgsConstructor
import lombok.Data
import lombok.experimental.Accessors

@AllArgsConstructor
@Data
@Accessors(fluent = true)
class PlayerAbilities {
    private val isInvulnerable = false
    private val flying = false
    private val canFly = false
    private val canInstantlyBuild = false
    private val mayBuild = true
    private val flySpeed = 0.05f
    private val walkSpeed = 0.1f
}
