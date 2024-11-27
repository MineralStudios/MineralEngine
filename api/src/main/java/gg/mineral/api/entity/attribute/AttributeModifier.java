package gg.mineral.api.entity.attribute;

import org.jetbrains.annotations.NotNull;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
@Getter
public class AttributeModifier {
    private final UUID id;
    private final String name;
    private final float amount;
    private final AttributeOperation operation;

    /**
     * Creates a new modifier with a random id.
     *
     * @param name      the name of this modifier
     * @param amount    the value of this modifier
     * @param operation the operation to apply this modifier with
     */
    public AttributeModifier(@NotNull String name, float amount, @NotNull AttributeOperation operation) {
        this(UUID.randomUUID(), name, amount, operation);
    }
}