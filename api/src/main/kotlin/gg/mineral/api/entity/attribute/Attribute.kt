package gg.mineral.api.entity.attribute

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap

class Attribute(key: String, shareWithClient: Boolean, defaultValue: Float, maxValue: Float) {
    /**
     * Gets the attribute unique key.
     *
     * @return the attribute key
     */
    val key: String

    /**
     * Gets the attribute default value that should be applied.
     *
     * @return the attribute default value
     */
    val defaultValue: Float

    /**
     * Gets the maximum value applicable to an entity for this attribute.
     *
     * @return the maximum value of this attribute
     */
    val maxValue: Float

    /**
     * Gets whether this attribute's instances should be sent to clients.
     *
     * @return if this attribute is to be shared
     */
    val isShared: Boolean

    /**
     * Create a new attribute with a given key and default.
     *
     *
     * By default, this attribute will be sent to the client.
     *
     *
     * @param key          the attribute registry key
     * @param defaultValue the default value
     * @param maxValue     the maximum allowed value
     */
    constructor(key: String, defaultValue: Float, maxValue: Float) : this(key, true, defaultValue, maxValue)

    /**
     * Create a new attribute with a given key and default.
     *
     * @param key             the attribute registry key
     * @param shareWithClient whether to send this attribute to the client
     * @param defaultValue    the default value
     * @param maxValue        the maximum allowed value
     */
    init {
        require(!(defaultValue > maxValue)) { "Default value cannot be greater than the maximum allowed" }

        this.key = key
        this.isShared = shareWithClient
        this.defaultValue = defaultValue
        this.maxValue = maxValue
    }

    /**
     * Register this attribute.
     *
     * @return this attribute
     * @see .fromKey
     * @see .values
     */
    fun register(): Attribute {
        ATTRIBUTES[key] = this
        return this
    }

    @JvmRecord
    data class Property(val value: Double, val modifiers: Collection<AttributeModifier>)
    
    companion object {
        private val ATTRIBUTES: MutableMap<String, Attribute> = Object2ObjectOpenHashMap()

        val MAX_HEALTH: Attribute = (Attribute("generic.maxHealth", true, 20f, Float.MAX_VALUE))
            .register()
        val FOLLOW_RANGE: Attribute = (Attribute("generic.followRange", true, 32f, 2048f)).register()
        val KNOCKBACK_RESISTANCE: Attribute = (Attribute("generic.knockbackResistance", true, 0f, 1f))
            .register()
        val MOVEMENT_SPEED: Attribute = (Attribute(
            "generic.movementSpeed", true, 0.1f,
            Float.MAX_VALUE
        )).register()
        val ATTACK_DAMAGE: Attribute = (Attribute("generic.attackDamage", true, 2f, Float.MAX_VALUE))
            .register()
        val HORSE_JUMP_STRENGTH: Attribute = (Attribute("horse.jumpStrength", true, 0.7f, 2f)).register()
        val ZOMBIE_SPAWN_REINFORCEMENTS: Attribute = (Attribute(
            "zombie.spawnReinforcements", true, 0f,
            1f
        )).register()

        /**
         * Retrieves an attribute by its key.
         *
         * @param key the key of the attribute
         * @return the attribute for the key or null if not any
         */
        fun fromKey(key: String): Attribute? {
            return ATTRIBUTES[key]
        }

        /**
         * Retrieves all registered attributes.
         *
         * @return an array containing all registered attributes
         */
        fun values(): Array<Attribute> {
            return ATTRIBUTES.values.toTypedArray<Attribute>()
        }

        /**
         * Retrieves registered attributes that are shared with the client.
         *
         * @return an array containing registered, sharable attributes
         */
        fun sharedAttributes(): Array<Attribute> {
            return ATTRIBUTES.values.stream()
                .filter { obj: Attribute -> obj.isShared }
                .toArray { arrayOfNulls<Attribute>(it) }
        }
    }
}