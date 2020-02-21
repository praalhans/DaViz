package com.aexiz.daviz.simulation.algorithm.information;

// TODO Rename if too confusing, but keep the Information prefix
public class InfoInformation extends AbstractMessageInformation {
    @Override
    public boolean equals(Object obj) {
        return obj instanceof InfoInformation;
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
