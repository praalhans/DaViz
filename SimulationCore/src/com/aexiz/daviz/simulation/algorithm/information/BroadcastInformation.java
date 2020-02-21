package com.aexiz.daviz.simulation.algorithm.information;

public class BroadcastInformation extends AbstractMessageInformation {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof BroadcastInformation;
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