package com.aexiz.daviz.simulation.algorithm.information;

import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractInformation implements Information {
    protected Map<String, String> parameters;

    public AbstractInformation() {
        validateObject();
        parameters = new HashMap<>();
    }

    public AbstractInformation(Map<String, String> parameters) {
        validateObject();
        this.parameters = parameters;
    }

    @Override
    public abstract String toString();

    @Override
    public void buildProperties(PropertyBuilder builder) {
        parameters.forEach(builder::simpleProperty);
    }

    private void validateObject() {
        if (!(this instanceof MessageInformation ||
                this instanceof ResultInformation ||
                this instanceof StateInformation ||
                this instanceof PropertyVisitor)) {
            throw new Error("Invalid information type. Information classes must implement one specific information interface");
        }
    }
}
