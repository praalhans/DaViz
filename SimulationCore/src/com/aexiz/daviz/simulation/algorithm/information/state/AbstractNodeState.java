package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.Node;
import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

public class AbstractNodeState extends AbstractInformation implements PropertyVisitor {
    protected Node node;
    protected String state;

    public AbstractNodeState(Node node, String state) {
        super();
        this.node = node;
        this.state = state;
    }

    @Override
    public String toString() {
        return state + "<" + node + ">";
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty(state + ":", node.getLabel());
    }
}
