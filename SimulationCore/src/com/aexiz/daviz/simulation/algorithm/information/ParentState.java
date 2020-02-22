package com.aexiz.daviz.simulation.algorithm.information;

import com.aexiz.daviz.simulation.Channel;

public class ParentState extends AbstractInformation implements PropertyVisitor {
    protected Channel channel;

    public ParentState(Channel channel) {
        super();
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "Parent<" + channel + ">";
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("Parent:", channel.to.getLabel());
    }
}
