package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

public class RepliedState extends AbstractInformation implements PropertyVisitor {
    protected Channel channel;

    public RepliedState(Channel channel) {
        super();
        this.channel = channel;
    }

    @Override
    public String toString() {
        return "Replied<" + channel + ">";
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", "Replied");
        builder.simpleProperty("To:", channel.to.getLabel());
    }
}
