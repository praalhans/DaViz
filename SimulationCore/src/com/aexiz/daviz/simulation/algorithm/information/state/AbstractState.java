package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;

import java.util.Map;

public abstract class AbstractState extends AbstractInformation implements PropertyVisitor {
    protected String state;

    public AbstractState(String state, Map<String, String> parameters) {
        super(parameters);
        this.state = state;
    }

    public AbstractState(String state) {
        super(Map.of("", state));
        this.state = state;
    }

    @Override
    public String toString() {
        return state;
    }
}
