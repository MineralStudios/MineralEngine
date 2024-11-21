package gg.mineral.server.entity.attribute;

import java.util.Collection;

public record Property(double value, Collection<AttributeModifier> modifiers) {
}
