package com.aexiz.daviz.simulation.algorithm.information.message;

import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;

import java.util.Map;

public abstract class AbstractMessage extends AbstractInformation implements MessageInformation {
    protected String message;

    public AbstractMessage(String message, Map<String, String> parameters) {
        super(parameters);
        this.message = message;
    }

    public AbstractMessage(String message) {
        super();
        this.message = message;
    }

    @Override
    public abstract boolean equals(Object obj);

    @Override
    public String toString() {
        return "*" + message + "*";
    }
}
