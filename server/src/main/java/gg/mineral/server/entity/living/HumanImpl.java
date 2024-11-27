package gg.mineral.server.entity.living;

import gg.mineral.api.entity.living.Human;
import gg.mineral.api.entity.living.human.property.Gamemode;
import gg.mineral.server.entity.LivingImpl;
import gg.mineral.server.entity.living.human.PlayerImpl;
import gg.mineral.server.network.packet.play.bidirectional.AnimationPacket;
import gg.mineral.server.world.WorldImpl;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

public class HumanImpl extends LivingImpl implements Human {
    @Getter
    @Setter
    private boolean sprinting;
    @Getter
    @Setter
    private boolean extraKnockback;
    @Getter
    @Setter
    protected Gamemode gamemode = Gamemode.SURVIVAL;
    protected final IntSet entityRemoveIds = new IntOpenHashSet();

    @Getter
    protected final Int2ObjectOpenHashMap<int[]> visibleEntities = new Int2ObjectOpenHashMap<>() {
        @Override
        public int[] remove(int key) {
            val value = super.remove(key);
            if (value != null)
                entityRemoveIds.add(key);
            return value;
        }
    };
    private boolean swingingArm;
    private int swingingTicks;

    public HumanImpl(int id, WorldImpl world) {
        super(id, world);
        this.width = 0.6f;
        this.height = 1.8f;
    }

    @Override
    public void swingArm() {
        for (int id : visibleEntities.keySet()) {
            val player = server.getPlayers().get(id);

            if (player != null)
                player.updateArm(this);
        }
    }

    public void updateArm(HumanImpl human) {
        if (!human.swingingArm || human.swingingTicks >= human.swingSpeed() / 2
                || human.swingingTicks < 0) {
            human.swingingTicks = -1;
            human.swingingArm = true;

            if (this instanceof PlayerImpl player)
                player.getConnection().queuePacket(new AnimationPacket(human.getId(), (short) 0));
        }

    }

    private int swingSpeed() {
        /*
         * return this.hasEffect(PotionEffect.HASTE)
         * ? 6 - (1 + this.getEffect(PotionEffect.HASTE).getAmplifier()) * 1
         * : (this.hasEffect(PotionEffect.MINING_FATIGUE)
         * ? 6 + (1 + this.getEffect(PotionEffect.MINING_FATIGUE).getAmplifier()) * 2
         * : 6);
         */
        return 6;
    }

    protected void tickArm() {
        int i = this.swingSpeed();

        if (this.swingingArm) {
            ++this.swingingTicks;
            if (this.swingingTicks >= i) {
                this.swingingTicks = 0;
                this.swingingArm = false;
            }
        } else
            this.swingingTicks = 0;
    }

    @Override
    public void tickAsync() {
        super.tickAsync();
    }

}
