package gg.mineral.server.entity.attribute;

import java.util.UUID;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class Modifier {
    final String name;
    final UUID uuid;
    final double amount;
    final byte operation;
}
