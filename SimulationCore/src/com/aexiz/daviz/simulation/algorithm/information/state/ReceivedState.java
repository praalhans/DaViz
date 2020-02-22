package com.aexiz.daviz.simulation.algorithm.information.state;

import com.aexiz.daviz.simulation.Channel;

import java.util.Map;

public class ReceivedState extends AbstractViewpointState {
    public ReceivedState(Channel channel) {
        super(
                channel,
                "Received",
                Map.of(
                        "", "Received",
                        "From", channel.to.getLabel()
                )
        );
    }
}
