package gg.mineral.server.entity.attribute;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Property {
    double value;
    List<Modifier> modifiers;
}
