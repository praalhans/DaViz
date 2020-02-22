package com.aexiz.daviz.simulation.algorithm.information.result;

import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

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
