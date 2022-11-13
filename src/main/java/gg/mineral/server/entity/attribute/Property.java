package gg.mineral.server.entity.attribute;

import java.util.List;

public class Property {
    double value;
    List<Modifier> modifiers;

    public Property(double value, List<Modifier> modifiers) {
        this.value = value;
        this.modifiers = modifiers;
    }

    public double getValue() {
        return value;
    }

    public List<Modifier> getModifiers() {
        return modifiers;
    }
}
