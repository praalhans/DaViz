package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

public class UndefinedState extends AbstractInformation implements PropertyVisitor {
    @Override
    public String toString() {
        return "Undefined";
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", "Undefined");
    }
}
