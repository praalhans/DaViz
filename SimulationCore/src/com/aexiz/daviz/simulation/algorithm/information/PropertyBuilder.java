package com.aexiz.daviz.simulation.algorithm.information;

import com.aexiz.daviz.simulation.algorithm.information.state.PropertyVisitor;

public interface PropertyBuilder {

    void simpleProperty(String name, String value);

    void compoundProperty(String name, PropertyVisitor visitor);

}
