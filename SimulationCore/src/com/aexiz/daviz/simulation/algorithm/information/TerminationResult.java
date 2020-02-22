package com.aexiz.daviz.simulation.algorithm.information;

public class TerminationResult extends AbstractInformation implements ResultInformation {
    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", "Terminated");
    }

    @Override
    public String toString() {
        return "Terminated";
    }
}
