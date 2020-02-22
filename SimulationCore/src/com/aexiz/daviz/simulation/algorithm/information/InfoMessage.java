package com.aexiz.daviz.simulation.algorithm.information;

public class InfoMessage extends AbstractMessageInformation {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof InfoMessage;
    }

    @Override
    public String toString() {
        return "*info*";
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", "Info");
    }
}
