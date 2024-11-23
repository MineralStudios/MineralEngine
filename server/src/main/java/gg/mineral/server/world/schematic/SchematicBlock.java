package gg.mineral.server.world.schematic;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@RequiredArgsConstructor
@Value
public class SchematicBlock {
    int x, y, z, type;
    byte data;
}