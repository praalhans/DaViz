package com.aexiz.daviz.simulation.algorithm.information;

public interface PropertyBuilder {

    void simpleProperty(String name, String value);

    void compoundProperty(String name, PropertyVisitor visitor);

}
