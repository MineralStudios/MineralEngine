package gg.mineral.server.entity.attribute;

import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@Value
public class Modifier {
    String name;
    UUID uuid;
    double amount;
    byte operation;
}
