package gg.mineral.server.entity.attribute;

import java.util.UUID;

public class Modifier {
    final String name;
    final UUID uuid;
    final double amount;
    final byte operation;

    public Modifier(String name, UUID uuid, double amount, byte operation) {
        this.name = name;
        this.uuid = uuid;
        this.amount = amount;
        this.operation = operation;
    }

    public String getName() {
        return name;
    }

    public UUID getUUID() {
        return uuid;
    }

    public double getAmount() {
        return amount;
    }

    public byte getOperation() {
        return operation;
    }
}
