package com.aexiz.daviz.simulation.algorithm.information.message;

import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

public class AckMessage extends AbstractMessageInformation {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof AckMessage;
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
