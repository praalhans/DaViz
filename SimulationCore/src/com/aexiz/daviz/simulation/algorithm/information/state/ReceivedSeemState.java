package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

public class ReceivedSeemState extends AbstractInformation implements PropertyVisitor {
    protected Channel channel;
    protected boolean seem;

    public ReceivedSeemState(Channel channel, boolean seem) {
        super();
        this.channel = channel;
        this.seem = seem;
    }

    @Override
    public String toString() {
        return "ReceivedSeen<" + channel + ">";
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", "Received");
        builder.simpleProperty("Seen token?", seem ? "true" : "false");
        builder.simpleProperty("From:", channel.to.getLabel());
    }
}
