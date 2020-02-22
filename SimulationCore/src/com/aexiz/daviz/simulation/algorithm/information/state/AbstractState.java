package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

public abstract class AbstractState extends AbstractInformation implements PropertyVisitor {
    protected String state;

    public AbstractState() {
        super();
        state = "Default State";
    }

    public AbstractState(String state) {
        super();
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", state);
    }
}
