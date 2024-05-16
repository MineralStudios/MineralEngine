package gg.mineral.server.network.packet.play.serverbound;

import gg.mineral.server.command.impl.KnockbackCommand;
import gg.mineral.server.entity.living.human.Player;
import gg.mineral.server.entity.manager.EntityManager;
import gg.mineral.server.network.connection.Connection;
import gg.mineral.server.network.packet.Packet;
import gg.mineral.server.network.packet.play.clientbound.EntityStatusPacket;
import gg.mineral.server.network.packet.play.clientbound.EntityVelocityPacket;
import gg.mineral.server.network.packet.play.clientbound.SoundEffectPacket;
import io.netty.buffer.ByteBuf;

public class UseEntityPacket implements Packet.INCOMING {

    int target;
    byte mouse;

    @Override
    public void received(Connection connection) {
        if (mouse == 1) { // left click
            if (target != -1) {
                EntityManager.getEntity(target).ifPresent(entity -> {
                    if (entity instanceof Player p && p.getCurrentTick() - p.getLastDamaged() >= 10) {
                        p.setLastDamaged(p.getCurrentTick());
                        // connection.sendPacket(new AnimationPacket(p.getId(), (short) 1));
                        EntityStatusPacket statusPacket = new EntityStatusPacket(p.getId(), (byte) 2);
                        p.getConnection().queuePacket(statusPacket);
                        connection.queuePacket(statusPacket);
                        connection.queuePacket(new SoundEffectPacket("game.player.hurt",
                                p.getX(), p.getY(),
                                p.getZ(), 1.0f, (entity.getRandom().nextFloat()
                                        - entity.getRandom().nextFloat()) * 0.2F + 1.0F));

                        EntityManager.get(connection).ifPresent(attacker -> {
                            double motX = p.getMotX();
                            double motY = p.getMotY();
                            double motZ = p.getMotZ();
                            double x = KnockbackCommand.x;
                            double y = KnockbackCommand.y;
                            double z = KnockbackCommand.z;

                            double extraX = KnockbackCommand.extraX;
                            double extraY = KnockbackCommand.extraY;
                            double extraZ = KnockbackCommand.extraZ;

                            double yLimit = KnockbackCommand.yLimit;

                            double friction = KnockbackCommand.friction;

                            motX /= friction;
                            motY /= friction;
                            motZ /= friction;

                            float angle = (float) Math.toRadians(attacker.getYaw());
                            double sin = -Math.sin(angle);
                            double cos = Math.cos(angle);

                            motX += x * sin;
                            motY += y;
                            motZ += z * cos;

                            if (motY > yLimit) {
                                motY = yLimit;
                            }

                            if (attacker.isExtraKnockback()) {
                                motX += extraX * sin;
                                motY += extraY;
                                motZ += extraZ * cos;
                                attacker.setMotX(attacker.getMotX() * 0.6);
                                attacker.setMotZ(attacker.getMotZ() * 0.6);
                                attacker.setExtraKnockback(false);
                            }
                            p.getConnection().queuePacket(new EntityVelocityPacket(target, motX, motY, motZ));
                        });

                    }
                });
            }
        }
    }

    @Override
    public void deserialize(ByteBuf is) {
        target = is.readInt();
        mouse = is.readByte();
    }
}
