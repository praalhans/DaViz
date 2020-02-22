package com.aexiz.daviz.simulation.algorithm.information;

public class DecidedInformation extends AbstractInformation implements ResultInformation {
    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", "Decided");
    }

    @Override
    public String toString() {
        return "Decided";
    }

}