package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

import java.util.Map;

public abstract class AbstractState extends AbstractInformation implements PropertyVisitor {
    protected String state;
    Map<String, String> parameters;

    public AbstractState(String state, Map<String, String> parameters) {
        super();
        this.state = state;
        this.parameters = parameters;
    }

    public AbstractState(String state) {
        this.state = state;
        parameters = Map.of("", state);
    }

    @Override
    public String toString() {
        return state;
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        parameters.forEach(builder::simpleProperty);
    }
}
