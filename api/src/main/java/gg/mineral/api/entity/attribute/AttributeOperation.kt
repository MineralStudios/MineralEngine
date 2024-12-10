package gg.mineral.api.entity.attribute

import lombok.Getter
import lombok.RequiredArgsConstructor

@RequiredArgsConstructor
@Getter
enum class AttributeOperation {
    ADDITION(0),
    MULTIPLY_BASE(1),
    MULTIPLY_TOTAL(2);

    private val id = 0

    companion object {
        private val VALUES = arrayOf(
            ADDITION, MULTIPLY_BASE,
            MULTIPLY_TOTAL
        )

        fun fromId(id: Int): AttributeOperation? {
            if (id >= 0 && id < VALUES.size) return VALUES[id]

            return null
        }
    }
}