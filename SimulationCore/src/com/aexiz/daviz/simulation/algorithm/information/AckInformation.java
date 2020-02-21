package com.aexiz.daviz.simulation.algorithm.information;

public class AckInformation extends AbstractMessageInformation {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AckInformation;
    }

    @Override
    public String toString() {
        return "*ack*";
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", "Ack");
    }
}
