package com.aexiz.daviz.simulation.algorithm.wave.tree;

import com.aexiz.daviz.simulation.Channel;
import com.aexiz.daviz.simulation.algorithm.information.state.AckDeciderState;

public class TreeAckSpreader extends AckDeciderState {
    public TreeAckSpreader(Channel channel) {
        super(channel);
    }
}