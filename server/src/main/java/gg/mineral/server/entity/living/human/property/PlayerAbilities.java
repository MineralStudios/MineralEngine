package gg.mineral.server.entity.living.human.property;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Data
@Accessors(fluent = true)
public class PlayerAbilities {
    private boolean isInvulnerable, flying, canFly, canInstantlyBuild, mayBuild = true;
    private float flySpeed = 0.05f, walkSpeed = 0.1f;
}
