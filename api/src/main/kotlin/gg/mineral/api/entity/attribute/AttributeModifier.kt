package gg.mineral.api.entity.attribute

import java.util.*

class AttributeModifier(
    val id: UUID = UUID.randomUUID(),
    val name: String,
    val amount: Float,
    val operation: AttributeOperation
)
