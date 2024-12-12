package gg.mineral.api.entity.attribute

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import java.util.*
import java.util.function.Consumer
import kotlin.math.min

/**
 * Represents an instance of an attribute and its modifiers.
 */
open class AttributeInstance(
    val attribute: Attribute,
    private val propertyChangeListener: Consumer<AttributeInstance>?
) {
    val modifiers: MutableMap<UUID, AttributeModifier> = Object2ObjectOpenHashMap()

    var baseValue: Float = attribute.defaultValue
        set(value) {
            if (field != value) {
                field = value
                refreshCachedValue()
            }
        }

    /**
     * Gets the value of this instance calculated with modifiers applied.
     *
     * @return the attribute value
     */
    var value: Float = 0.0f
        private set

    init {
        this.baseValue = attribute.defaultValue
        refreshCachedValue()
    }

    /**
     * Add a modifier to this instance.
     *
     * @param modifier the modifier to add
     */
    fun addModifier(modifier: AttributeModifier) {
        if (modifiers.putIfAbsent(modifier.id, modifier) == null) refreshCachedValue()
    }

    /**
     * Remove a modifier from this instance.
     *
     * @param modifier the modifier to remove
     */
    fun removeModifier(modifier: AttributeModifier) {
        if (modifiers.remove(modifier.id) != null) refreshCachedValue()
    }

    /**
     * Get the modifiers applied to this instance.
     *
     * @return the modifiers.
     */
    private fun getModifiers(): Collection<AttributeModifier> {
        return modifiers.values
    }

    /**
     * Recalculate the value of this attribute instance using the modifiers.
     */
    private fun refreshCachedValue() {
        val modifiers = getModifiers()
        var base: Float = baseValue

        for (modifier in modifiers.stream()
            .filter { mod: AttributeModifier -> mod.operation == AttributeOperation.ADDITION }
            .toArray { arrayOfNulls<AttributeModifier>(it) }) base += modifier?.amount ?: 0.0f

        var result = base

        for (modifier in modifiers.stream()
            .filter { mod: AttributeModifier -> mod.operation == AttributeOperation.MULTIPLY_BASE }
            .toArray { arrayOfNulls<AttributeModifier>(it) }) result += (base * (modifier?.amount ?: 0.0f))

        for (modifier in modifiers.stream()
            .filter { mod: AttributeModifier -> mod.operation == AttributeOperation.MULTIPLY_TOTAL }
            .toArray { arrayOfNulls<AttributeModifier>(it) }) result *= (1.0f + (modifier?.amount ?: 0.0f))

        this.value = min(result.toDouble(), attribute.maxValue.toDouble()).toFloat()

        // Signal entity
        propertyChangeListener?.accept(this)
    }
}