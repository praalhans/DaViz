package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.algorithm.information.PropertyBuilder;

public class ReceivedState extends AbstractChannelState {
    public ReceivedState(Channel channel) {
        super(channel, "Received", false, true, true);
    }
}
