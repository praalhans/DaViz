package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.algorithm.information.AbstractInformation;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

public class AbstractChannelState extends AbstractInformation implements PropertyVisitor {
    protected Channel channel;
    protected String state;
    protected boolean showToNode;
    protected boolean showFromNode;
    protected boolean showFromNodeAsReverse;

    public AbstractChannelState(Channel channel, String state, boolean showToNode, boolean showFromNode) {
        super();
        this.channel = channel;
        this.state = state;
        this.showToNode = showToNode;
        this.showFromNode = showFromNode;
        this.showFromNodeAsReverse = false;
    }

    public AbstractChannelState(Channel channel, String state, boolean showToNode, boolean showFromNode, boolean showFromNodeAsReverse) {
        this.channel = channel;
        this.state = state;
        this.showToNode = showToNode;
        this.showFromNode = showFromNode;
        this.showFromNodeAsReverse = showFromNodeAsReverse;
    }

    @Override
    public String toString() {
        return state + "<" + channel + ">";
    }

    @Override
    public void buildProperties(PropertyBuilder builder) {
        builder.simpleProperty("", state);
        if (showFromNode) {
            String value = showFromNodeAsReverse ? channel.to.getLabel() : channel.from.getLabel();
            builder.simpleProperty("From:", value);
        }
        if (showToNode) builder.simpleProperty("To:", channel.to.getLabel());
    }
}
