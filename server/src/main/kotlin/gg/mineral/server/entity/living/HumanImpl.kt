package gg.mineral.server.entity.living

import gg.mineral.api.entity.living.Human
import gg.mineral.api.entity.living.human.property.Gamemode
import gg.mineral.server.entity.LivingImpl
import gg.mineral.server.entity.living.human.PlayerImpl
import gg.mineral.server.network.packet.play.bidirectional.AnimationPacket
import gg.mineral.server.world.WorldImpl
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import it.unimi.dsi.fastutil.ints.IntOpenHashSet

open class HumanImpl(id: Int, world: WorldImpl, override val name: String) :
    LivingImpl(id, world), Human {
    val entityRemoveIds = IntOpenHashSet()

    val visibleEntities = Int2ObjectOpenHashMap<IntArray>()

    override var extraKnockback: Boolean = false

    override var gamemode: Gamemode = Gamemode.SURVIVAL

    override var sprinting = false
    private var swingingArm = false
    private var swingingTicks = 0

    init {
        this.width = 0.6f
        this.height = 1.8f
    }

    override fun swingArm() {
        val fastIterator = visibleEntities.int2ObjectEntrySet().fastIterator()
        while (fastIterator.hasNext()) {
            val entry = fastIterator.next()
            val id = entry.intKey

            world.getPlayer(id)?.updateArm(this@HumanImpl)
        }
    }

    fun updateArm(human: HumanImpl) {
        if (!human.swingingArm || human.swingingTicks >= human.swingSpeed() / 2 || human.swingingTicks < 0) {
            human.swingingTicks = -1
            human.swingingArm = true

            if (this is PlayerImpl) this.connection.queuePacket(AnimationPacket(human.id, 0.toShort()))
        }
    }

    private fun swingSpeed(): Int {
        /*
         * return this.hasEffect(PotionEffect.HASTE)
         * ? 6 - (1 + this.getEffect(PotionEffect.HASTE).getAmplifier()) * 1
         * : (this.hasEffect(PotionEffect.MINING_FATIGUE)
         * ? 6 + (1 + this.getEffect(PotionEffect.MINING_FATIGUE).getAmplifier()) * 2
         * : 6);
         */
        return 6
    }

    protected fun tickArm() {
        val i = this.swingSpeed()

        if (this.swingingArm) {
            ++this.swingingTicks
            if (this.swingingTicks >= i) {
                this.swingingTicks = 0
                this.swingingArm = false
            }
        } else this.swingingTicks = 0
    }
}
