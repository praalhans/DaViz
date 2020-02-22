package com.aexiz.daviz.simulation.algorithm.information;

public class BroadcastMessage extends AbstractMessageInformation {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BroadcastMessage;
    }

    @Override
    public String toString() {
        return "*broadcast*";
    }

    @Override
    public void buildProperties(PropertyBuilder visitor) {
        visitor.simpleProperty("", "Broadcast");
    }
}