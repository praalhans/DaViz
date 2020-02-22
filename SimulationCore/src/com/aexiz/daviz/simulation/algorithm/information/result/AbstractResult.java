package com.aexiz.daviz.simulation.algorithm.information.result;

import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;

import java.util.Map;

public abstract class AbstractResult extends AbstractInformation implements ResultInformation {
    protected String result;

    public AbstractResult(String result, Map<String, String> parameters) {
        super(parameters);
        this.result = result;
    }

    @Override
    public String toString() {
        return result;
    }
}
