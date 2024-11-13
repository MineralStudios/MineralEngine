package gg.mineral.server.entity.attribute;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Property {
    private double value, lastValue;
    private List<Modifier> modifiers;

    public void setValue(double value) {
        lastValue = this.value;
        this.value = value;
    }
}
