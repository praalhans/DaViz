package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

public class ReceivedState extends AbstractInformation implements PropertyVisitor {
    protected Channel channel;

    public ReceivedState(Channel channel) {
        super();
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "Received<" + channel + ">";
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", "Received");
        builder.simpleProperty("From:", channel.to.getLabel());
    }
}
