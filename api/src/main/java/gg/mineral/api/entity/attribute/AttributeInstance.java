package gg.mineral.api.entity.attribute;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.val;

/**
 * Represents an instance of an attribute and its modifiers.
 */
public class AttributeInstance {
    @Getter
    private final Attribute attribute;
    private final Map<UUID, AttributeModifier> modifiers = new Object2ObjectOpenHashMap<>();
    private final Consumer<AttributeInstance> propertyChangeListener;
    @Getter
    private float baseValue;
    private float cachedValue = 0.0f;

    public AttributeInstance(@NotNull Attribute attribute, @Nullable Consumer<AttributeInstance> listener) {
        this.attribute = attribute;
        this.propertyChangeListener = listener;
        this.baseValue = attribute.getDefaultValue();
        refreshCachedValue();
    }

    /**
     * Sets the base value of this instance.
     *
     * @param baseValue the new base value
     * @see #getBaseValue()
     */
    public void setBaseValue(float baseValue) {
        if (this.baseValue != baseValue) {
            this.baseValue = baseValue;
            refreshCachedValue();
        }
    }

    /**
     * Add a modifier to this instance.
     *
     * @param modifier the modifier to add
     */
    public void addModifier(@NotNull AttributeModifier modifier) {
        if (modifiers.putIfAbsent(modifier.getId(), modifier) == null)
            refreshCachedValue();
    }

    /**
     * Remove a modifier from this instance.
     *
     * @param modifier the modifier to remove
     */
    public void removeModifier(@NotNull AttributeModifier modifier) {
        if (modifiers.remove(modifier.getId()) != null)
            refreshCachedValue();
    }

    /**
     * Get the modifiers applied to this instance.
     *
     * @return the modifiers.
     */
    @NotNull
    public Collection<AttributeModifier> getModifiers() {
        return modifiers.values();
    }

    /**
     * Gets the value of this instance calculated with modifiers applied.
     *
     * @return the attribute value
     */
    public float getValue() {
        return cachedValue;
    }

    /**
     * Recalculate the value of this attribute instance using the modifiers.
     */
    protected void refreshCachedValue() {
        final Collection<AttributeModifier> modifiers = getModifiers();
        float base = getBaseValue();

        for (val modifier : modifiers.stream().filter(mod -> mod.getOperation() == AttributeOperation.ADDITION)
                .toArray(AttributeModifier[]::new))
            base += modifier.getAmount();

        float result = base;

        for (val modifier : modifiers.stream().filter(mod -> mod.getOperation() == AttributeOperation.MULTIPLY_BASE)
                .toArray(AttributeModifier[]::new))
            result += (base * modifier.getAmount());

        for (val modifier : modifiers.stream().filter(mod -> mod.getOperation() == AttributeOperation.MULTIPLY_TOTAL)
                .toArray(AttributeModifier[]::new))
            result *= (1.0f + modifier.getAmount());

        this.cachedValue = Math.min(result, getAttribute().getMaxValue());

        // Signal entity
        if (propertyChangeListener != null)
            propertyChangeListener.accept(this);
    }
}