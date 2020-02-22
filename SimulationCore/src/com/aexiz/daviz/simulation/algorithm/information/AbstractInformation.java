package com.aexiz.daviz.simulation.algorithm.information;

public abstract class AbstractInformation implements Information {

    public AbstractInformation() {
        if (!(this instanceof MessageInformation ||
                this instanceof ResultInformation ||
                this instanceof StateInformation ||
                this instanceof PropertyVisitor)){
            throw new Error("Invalid information type. Information classes must implement one specific information interface");
        }
    }

    @Override
    public abstract String toString();
}
