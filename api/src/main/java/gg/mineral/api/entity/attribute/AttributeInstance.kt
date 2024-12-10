package gg.mineral.api.entity.attribute

import gg.mineral.api.entity.attribute.Attribute.maxValue
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap
import lombok.Getter
import java.util.*
import java.util.function.Consumer
import kotlin.math.min

/**
 * Represents an instance of an attribute and its modifiers.
 */
class AttributeInstance(
    @field:Getter private val attribute: Attribute,
    private val propertyChangeListener: Consumer<AttributeInstance>?
) {
    private val modifiers: MutableMap<UUID, AttributeModifier?> = Object2ObjectOpenHashMap()

    @Getter
    private var baseValue: Float

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
     * Sets the base value of this instance.
     *
     * @param baseValue the new base value
     * @see .getBaseValue
     */
    fun setBaseValue(baseValue: Float) {
        if (this.baseValue != baseValue) {
            this.baseValue = baseValue
            refreshCachedValue()
        }
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
    fun getModifiers(): Collection<AttributeModifier?> {
        return modifiers.values
    }

    /**
     * Recalculate the value of this attribute instance using the modifiers.
     */
    protected fun refreshCachedValue() {
        val modifiers = getModifiers()
        var base: Float = getBaseValue()

        for (modifier in modifiers.stream()
            .filter { mod: AttributeModifier? -> mod!!.operation == AttributeOperation.ADDITION }
            .toArray<AttributeModifier> { _Dummy_.__Array__() }) base += modifier.amount

        var result = base

        for (modifier in modifiers.stream()
            .filter { mod: AttributeModifier? -> mod!!.operation == AttributeOperation.MULTIPLY_BASE }
            .toArray<AttributeModifier> { _Dummy_.__Array__() }) result += (base * modifier.amount)

        for (modifier in modifiers.stream()
            .filter { mod: AttributeModifier? -> mod!!.operation == AttributeOperation.MULTIPLY_TOTAL }
            .toArray<AttributeModifier> { _Dummy_.__Array__() }) result *= (1.0f + modifier.amount)

        this.value = min(result.toDouble(), getAttribute().maxValue.toDouble()).toFloat()

        // Signal entity
        propertyChangeListener?.accept(this)
    }
}