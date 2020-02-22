package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.Channel;

public class RepliedState extends AbstractChannelState {
    public RepliedState(Channel channel) {
        super(channel, "Replied", true, false);
    }
}
