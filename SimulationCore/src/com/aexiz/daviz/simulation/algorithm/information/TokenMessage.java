package com.aexiz.daviz.simulation.algorithm.information;

public class TokenMessage extends AbstractMessageInformation {
    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", "Token");
    }

    @Override
    public String toString() {
        return "*token*";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof TokenMessage;
    }
}
