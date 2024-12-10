package gg.mineral.server.world.schematic

import lombok.RequiredArgsConstructor
import lombok.Value

@RequiredArgsConstructor
@Value
class SchematicBlock {
    var x: Int = 0
    var y: Int = 0
    var z: Int = 0
    var type: Int = 0
    var data: Byte = 0
}