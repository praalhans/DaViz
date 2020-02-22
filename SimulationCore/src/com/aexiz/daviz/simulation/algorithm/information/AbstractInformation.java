package com.aexiz.daviz.simulation.algorithm.information;

import com.aexiz.daviz.simulation.algorithm.information.message.MessageInformation;
import com.aexiz.daviz.simulation.algorithm.information.result.ResultInformation;
import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;
import com.aexiz.daviz.simulation.algorithm.information.state.StateInformation;

public abstract class AbstractInformation implements Information {

    public AbstractInformation() {
        if (!(this instanceof MessageInformation ||
                this instanceof ResultInformation ||
                this instanceof StateInformation ||
                this instanceof PropertyVisitor)) {
            throw new Error("Invalid information type. Information classes must implement one specific information interface");
        }
    }

    @Override
    public abstract String toString();
}
