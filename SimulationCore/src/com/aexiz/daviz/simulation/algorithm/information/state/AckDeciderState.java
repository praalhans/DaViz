package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.viewpoint.Channel;

import java.util.Map;

public class AckDeciderState extends AbstractViewpointState {
    public AckDeciderState(Channel channel) {
        super(
                channel,
                "Decider",
                Map.of(
                        "Decider:", channel.to.getLabel()
                )
        );
    }
}
