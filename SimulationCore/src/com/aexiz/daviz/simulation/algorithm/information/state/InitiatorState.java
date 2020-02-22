package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

public class InitiatorState extends AbstractInformation implements PropertyVisitor {
    @Override
    public String toString() {
        return "Initiator";
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", "Initiator");
    }
}
