package com.aexiz.daviz.simulation.algorithm.wave.tree;

import com.aexiz.daviz.simulation.algorithm.information.state.AckDeciderState;
import com.aexiz.daviz.simulation.viewpoint.Channel;

public class TreeAckSpreader extends AckDeciderState {
    public TreeAckSpreader(Channel channel) {
        super(channel);
    }
}
