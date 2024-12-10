package gg.mineral.api.entity.attribute

import lombok.Getter
import lombok.RequiredArgsConstructor
import java.util.*

@RequiredArgsConstructor
@Getter
class AttributeModifier {
    private val id: UUID? = null
    private val name: String? = null
    private val amount = 0f
    private val operation: AttributeOperation? = null

    /**
     * Creates a new modifier with a random id.
     *
     * @param name      the name of this modifier
     * @param amount    the value of this modifier
     * @param operation the operation to apply this modifier with
     */
    constructor(name: String, amount: Float, operation: AttributeOperation) : this(
        UUID.randomUUID(),
        name,
        amount,
        operation
    )
}